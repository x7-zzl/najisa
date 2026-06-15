package com.zzl.umr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.config.exception.CommonServiceException;
import com.zzl.umr.enums.UserDisEnabledEnum;
import com.zzl.umr.enums.UserRoleEnum;
import com.zzl.umr.mapper.BasicUserLoginMapper;
import com.zzl.umr.mapper.BasicUserMapper;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.BasicUserLogin;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.cdn.LoginCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.model.dto.UserProfileDTO;
import com.zzl.umr.service.BasicFileService;
import com.zzl.umr.service.BasicUserLoginService;
import com.zzl.umr.service.BasicUserService;
import com.zzl.umr.service.RedisService;
import com.zzl.umr.utils.EnumUtil;
import com.zzl.umr.utils.JwtUtils;
import com.zzl.umr.utils.NickNameGeneratorUtil;
import com.zzl.umr.utils.PasswordValidatorUtil;
import com.zzl.umr.utils.SnowflakeIdWorker;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zzl.umr.constants.MessageConstant.ENUM_GET_CODE_METHOD_NAME;
import static com.zzl.umr.constants.MessageConstant.ENUM_GET_NAME_METHOD_NAME;
import static com.zzl.umr.constants.RedisKeyFixConstant.USER_LOGIN_TOKEN_KEY_FIX;

/**
 * @author zhangzl
 * @description 用户信息表(BasicUser)表服务实现类
 * @date 2025/10/14 23:52
 */
@Service("basicUserService")
@Slf4j
public class BasicUserServiceImpl extends ServiceImpl<BasicUserMapper, BasicUser> implements BasicUserService {
    @Resource
    private BasicUserMapper basicUserMapper;

    @Resource
    private BasicUserLoginMapper basicUserLoginMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private BasicUserLoginService  basicUserLoginService;

    @Resource
    private RedisService redisService;

    @Resource
    private BasicFileService basicFileService;

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    @Override
    public BasicUser queryById(String userId) {
        return basicUserMapper.selectById(userId);
    }

    /**
     * 查询数据列表
     *
     * @param basicUser 查询参数
     * @return 实例对象集合
     */
    @Override
    public List<BasicUser> queryList(BasicUser basicUser) {
        //处理查询条件
        LambdaQueryWrapper<BasicUser> queryWrapper = new LambdaQueryWrapper<>();
        this.dealWrapper(queryWrapper, basicUser);

        List<BasicUser> basicUserList = basicUserMapper.selectList(queryWrapper);

        //处理数据
        this.dealDataList(basicUserList);

        return basicUserList;
    }


    /**
     * 新增数据
     *
     * @param basicUser 实例对象
     * @return 实例对象
     */
    @Override
    public boolean add(BasicUser basicUser) {

        basicUser.setId(SnowflakeIdWorker.newId());

        // 设置用户角色
        basicUser.setRoleId(UserRoleEnum.USER.getCode());

        // 设置账号不禁用
        basicUser.setIsEnabled(UserDisEnabledEnum.ENABLED.getCode());

        log.info("插入一条数据:{}", basicUser);

        return basicUserMapper.insert(basicUser) > 0;
    }

    /**
     * 批量新增数据
     *
     * @param basicUserList 数据列表
     * @return 批量新增结果
     */
    @Override
    public Integer batchAdd(List<BasicUser> basicUserList) {
        if (CollectionUtil.isNotEmpty(basicUserList)) {
            log.info("批量插入{}条数据", basicUserList.size());

            for (BasicUser basicUser : basicUserList) {
                basicUser.setId(SnowflakeIdWorker.newId());
            }

            boolean saveBatch = this.saveBatch(basicUserList);
            return saveBatch ? basicUserList.size() : 0;
        }
        return 0;
    }

    /**
     * 修改数据
     *
     * @param basicUser 实例对象
     * @return 实例对象
     */
    @Override
    public boolean updateById(BasicUser basicUser) {
        log.info("修改一条数据:{}", basicUser);

        return basicUserMapper.updateById(basicUser) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String userId) {
        log.info("删除一条数据，userId:{}", userId);
        return basicUserMapper.deleteById(userId) > 0;
    }

    /**
     * 批量删除数据
     *
     * @param idList 数据列表
     * @return 批量删除结果
     */
    @Transactional
    @Override
    public Integer batchDelete(List<String> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            log.info("批量删除{}条数据", idList.size());

            return basicUserMapper.deleteBatchIds(idList);
        }
        return 0;
    }


    @Override
    public HttpResult login(LoginCdn logInCdn) {
        log.info("开始 Security 登录验证:{}", logInCdn.getUserName());

        // 基本非空校验
        if (StringUtil.isNullOrEmpty(logInCdn.getUserName()) || StringUtil.isNullOrEmpty(logInCdn.getPassword())) {
            return HttpResult.fail("用户名或密码不能为空");
        }

        try {
            // 封装认证对象
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(logInCdn.getUserName().trim(), logInCdn.getPassword().trim());

            // 执行认证（底层会自动调用你的 UserDetailsServiceImpl.loadUserByUsername 并匹配密码）
            // 如果密码错误或用户不存在，这一步会抛出异常
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 认证成功，生成 JWT
            String token = JwtUtils.createToken(logInCdn.getUserName().trim());

            basicUserLoginService.asyncUpdateLoginInfo(logInCdn.getUserName().trim());

            // 获取用户信息返回给前端
            LambdaQueryWrapper<BasicUser> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(BasicUser::getUserName, logInCdn.getUserName().trim()).last("limit 1");
            BasicUser user = basicUserMapper.selectOne(userWrapper);

            LambdaQueryWrapper<BasicUserLogin> loginWrapper = new LambdaQueryWrapper<>();
            loginWrapper.eq(BasicUserLogin::getUserId, user.getId()).last("limit 1");
            BasicUserLogin userLogin = basicUserLoginMapper.selectOne(loginWrapper);

            JSONObject result = new JSONObject();
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("userName", user.getUserName());
            result.put("nickName", user.getNickName());
            if (userLogin != null) {
                result.put("userAvatar", userLogin.getUserAvatar());
            }
            return HttpResult.success(result);

        } catch (CommonServiceException e) {
            log.error("登录失败: {}", e.getMessage());
            return HttpResult.fail();
        }
    }

    /**
     * 提取出来的更新登录信息逻辑
     */
    private void updateLoginInfo(String userName) {
        LambdaQueryWrapper<BasicUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BasicUser::getUserName, userName).last("limit 1");
        BasicUser user = basicUserMapper.selectOne(queryWrapper);

        if (user != null) {
            Date now = new Date();
            LambdaQueryWrapper<BasicUserLogin> loginQueryWrapper = new LambdaQueryWrapper<>();
            loginQueryWrapper.eq(BasicUserLogin::getUserId, user.getId()).last("limit 1");
            BasicUserLogin userLogin = basicUserLoginMapper.selectOne(loginQueryWrapper);

            if (userLogin == null) {
                userLogin = new BasicUserLogin();
                userLogin.setId(SnowflakeIdWorker.newId());
                userLogin.setUserId(user.getId());
                userLogin.setFirstLoginTime(now);
                userLogin.setLastLoginTime(now);
                basicUserLoginMapper.insert(userLogin);
            } else {
                userLogin.setLastLoginTime(now);
                basicUserLoginMapper.updateById(userLogin);
            }
        }
    }

    /**
     * 注册
     *
     * @param logInCdn 注册参数
     * @return 注册结果
     */
    @Override
    public HttpResult<String> register(LoginCdn logInCdn) {
        log.info("注册参数:{}", logInCdn);
        //校验是否符合要求，不能为空，用户名和密码不能为空
        // 用户名非空
        if (StringUtil.isNullOrEmpty(logInCdn.getUserName())) {
            return HttpResult.fail("用户名不能为空");
        }
        // 密码非空
        if (StringUtil.isNullOrEmpty(logInCdn.getPassword())) {
            return HttpResult.fail("密码不能为空");
        }

        // 密码复杂度，至少需要数字，字母，特殊字符的其中任意两种组合，长度不能小于8位
        String passwordError = PasswordValidatorUtil.validatePassword(logInCdn.getPassword());
        if (!StringUtil.isNullOrEmpty(passwordError)) {
            return HttpResult.fail(passwordError);
        }

        // 去除首尾空格（避免用户输入空格导致的问题）
        String userName = logInCdn.getUserName().trim();
        String password = logInCdn.getPassword().trim();

        // 加密密码（推荐使用BCrypt）
        String encryptPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // 查询当前用户名在数据库中是否存在，如果存在则返回该用户已存在
        LambdaQueryWrapper<BasicUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BasicUser::getUserName, userName);
        queryWrapper.last("limit 1");
        BasicUser existUser = basicUserMapper.selectOne(queryWrapper);
        if (existUser != null) {
            log.warn("注册失败：账号{}已存在", userName);
            return HttpResult.fail("账号已存在");
        }

        BasicUser basicUser = new BasicUser();
        basicUser.setId(SnowflakeIdWorker.newId());
        basicUser.setUserName(userName);
        basicUser.setPassword(encryptPassword);
        // 昵称随机生成
        basicUser.setNickName(NickNameGeneratorUtil.generateNickname());

        boolean insertSuccess = this.add(basicUser);
        if (!insertSuccess) {
            log.error("注册失败：用户名{}插入数据库失败", userName);
            return HttpResult.fail("注册失败，请稍后重试");
        }

        log.info("注册成功：用户名{}", userName);
        return HttpResult.success("注册成功");
    }

    @Override
    public HttpResult logout() {
        SecurityContextHolder.getContext().getAuthentication();

        // 获取Token
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return HttpResult.fail("获取请求信息失败");
        }
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 获取Token剩余有效期
            long remainingTime = JwtUtils.getRemainingTime(token);

            //  如果还有剩余时间，加入黑名单
            if (remainingTime > 0) {
                // key = "jwt_blacklist:" + token
                redisService.setValueWithExpiry(USER_LOGIN_TOKEN_KEY_FIX + token, "invalid", remainingTime, TimeUnit.MILLISECONDS);
            }
        }

        // 清除 SecurityContext
        SecurityContextHolder.clearContext();

        return HttpResult.success("退出登录成功");
    }

    @Override
    public HttpResult<String> forgetPassword(LoginCdn logInCdn) {

        return null;
    }

    @Override
    public Page<BasicUser> queryListPage(BaseQueryCdn queryCdn) {
        Page<BasicUser> page = new Page<>(queryCdn.getPageNum(), queryCdn.getPageSize());

        LambdaQueryWrapper<BasicUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryCdn.getKeyword())) {
            wrapper.and(w -> w.like(BasicUser::getUserName, queryCdn.getKeyword())
                    .or()
                    .like(BasicUser::getNickName, queryCdn.getKeyword()));
        }
        // 根据更新时间倒序
        wrapper.orderByDesc(BasicUser::getUpdateTime);

        Page<BasicUser> pageData = basicUserMapper.selectPage(page, wrapper);

        this.dealDataList(pageData.getRecords());

        return pageData;

    }

    @Override
    public UserProfileDTO getUserProfile(String userId) {
        BasicUser user = basicUserMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        LambdaQueryWrapper<BasicUserLogin> loginWrapper = new LambdaQueryWrapper<>();
        loginWrapper.eq(BasicUserLogin::getUserId, userId).last("limit 1");
        BasicUserLogin userLogin = basicUserLoginMapper.selectOne(loginWrapper);

        UserProfileDTO profileDto = UserProfileDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .idCard(user.getIdCard())
                .age(user.getAge())
                .sex(user.getSex())
                .phoneNumber(user.getPhoneNumber())
                .emailAddress(user.getEmailAddress())
                .userAvatar(userLogin != null ? userLogin.getUserAvatar() : null)
                .build();

        return profileDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUserProfile(UserProfileDTO profileDto) {
        BasicUser user = new BasicUser();
        user.setId(profileDto.getId());
        user.setNickName(profileDto.getNickName());
        user.setIdCard(profileDto.getIdCard());
        user.setAge(profileDto.getAge());
        user.setSex(profileDto.getSex());
        user.setPhoneNumber(profileDto.getPhoneNumber());
        user.setEmailAddress(profileDto.getEmailAddress());
        int userUpdated = basicUserMapper.updateById(user);

        if (StringUtils.isNotBlank(profileDto.getUserAvatar())) {
            LambdaQueryWrapper<BasicUserLogin> loginWrapper = new LambdaQueryWrapper<>();
            loginWrapper.eq(BasicUserLogin::getUserId, profileDto.getId()).last("limit 1");
            BasicUserLogin userLogin = basicUserLoginMapper.selectOne(loginWrapper);

            if (userLogin != null) {
                String oldAvatar = userLogin.getUserAvatar();
                userLogin.setUserAvatar(profileDto.getUserAvatar());
                basicUserLoginMapper.updateById(userLogin);
                if (StringUtils.isNotBlank(oldAvatar) && !StringUtils.equals(oldAvatar, profileDto.getUserAvatar())) {
                    basicFileService.deleteLocalByUrl(oldAvatar);
                }
            } else {
                userLogin = new BasicUserLogin();
                userLogin.setId(SnowflakeIdWorker.newId());
                userLogin.setUserId(profileDto.getId());
                userLogin.setUserAvatar(profileDto.getUserAvatar());
                basicUserLoginMapper.insert(userLogin);
            }
        }

        return userUpdated > 0;
    }

    /**
     * 处理查询条件
     *
     * @param queryWrapper 查询条件
     * @param basicUser    查询条件
     */
    private void dealWrapper(LambdaQueryWrapper<BasicUser> queryWrapper, BasicUser basicUser) {

    }

    /**
     * 处理数据
     *
     * @param basicUserList 数据列表
     */
    private void dealDataList(List<BasicUser> basicUserList) {
        if (CollectionUtil.isNotEmpty(basicUserList)) {
            //遍历转换
            for (BasicUser basicUser : basicUserList) {
                //是否禁用
                if (basicUser.getIsEnabled() != null) {
                    basicUser.setIsEnabledStatus(
                            EnumUtil.getEnumNameByCode(UserDisEnabledEnum.class, basicUser.getIsEnabled(), ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME)
                    );
                }

                //管理员
                if (basicUser.getRoleId() != null) {
                    basicUser.setRoleName(
                            EnumUtil.getEnumNameByCode(UserRoleEnum.class, basicUser.getRoleId(), ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME)
                    );
                }

            }
        }
    }
}
