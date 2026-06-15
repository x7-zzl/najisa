# najisa-frontend
<!-- 加密 -->
npm install crypto-js

## Project setup
```
npm install -g @vue/cli

npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

### version
PS D:\code\najisa-frontend> node -v
v24.11.1
PS D:\code\najisa-frontend> npm -v
11.6.2



### 可能遇到的问题

1. 在此系统上禁止运行脚本
PS D:\code\najisa-frontend> npm install -g @vue/cli
npm : 无法加载文件 C:\Program Files\nodejs\npm.ps1，因为在此系统上禁止运行脚本。有关详细信息，请参阅 https:/go.microsoft.com
/fwlink/?LinkID=135170 中的 about_Execution_Policies。
所在位置 行:1 字符: 1
+ npm install -g @vue/cli
+ ~~~
    + CategoryInfo          : SecurityError: (:) []，PSSecurityException
    + FullyQualifiedErrorId : UnauthorizedAccess

解决问题：
以管理员身份打开 PowerShell

搜索 "PowerShell"

右键点击 → "以管理员身份运行"

更改执行策略（选择其中一种）：

powershell
临时更改（仅当前会话）
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process

永久更改（推荐）
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

或改为 Unrestricted
Set-ExecutionPolicy -ExecutionPolicy Unrestricted -Scope CurrentUser

重新运行
