# Git 介绍
> Git 是目前主流的开源分布式版本控制系统；具有可离线工作、轻量级分支等特点。
## 基础概念
* 工作区（Working Directory）用于存放目录、文件
* 暂存区（Stage）用于存放待提交内容
* 本地版本库（Local Repository）
* 远程版本库（Remote Repository）
* HEAD 指针
## 文件状态
> 任何一个文件，在 Git 内有三种状态：
* 已修改（modified）=> 工作目录
* 已暂存（staged）=> 暂存区
* 已提交（committed）=> 本地仓库
## 文件存储机制
> 包含 4 种对象及索引
* blob 保存一个文件一个版本的数据
* tree 表示一个目录，记录着目录中所有文件 blob 哈希值、文件名、子目录名及其它元数据；通过递归引用其它目录树，从而建立完整的目录树结构
* commit 一个提交保存每一次变化的元数据，每个提交指向一个 tree 对象
* tag 分为轻量标签和附注标签；

# Git 常用命令
## 基础配置
```
> git config --global user.name "xxx"
> git config --global user.email "xxx@yzw.cn"
> git config --list
```
## 仓库操作
```
# 创建仓库
> git init
> git remote add origin http://cmisgitlab01/dianshang/test.git
> git push --set-upstream origin master
# 克隆远程仓库
> git clone http://cmisgitlab01/dianshang/test.git
# 获取远程仓库更新
> git fetch
# 获取远程仓库更新，并合并对应远程分支到当前分支，等于 git fetch + merge
> git pull
# 推送本地分支到远程分支
> git push
> git push origin <branch>
# 删除远程分支
> git push origin :<branch_name>
> git push origin --delete <branch_name>
```
## 分支操作
```
# 创建分支
> git branch <branch_name>
# 查看分支
> git branch [-a] [-r]
# 切换到对应分支
> git checkout <branch_name>
# 基于当前分支创建新分支，并切换到新分支
> git checkout -b <branch_name>
# 删除本地分支
> git branch -d <branch_name>
# 添加修改内容到暂存区
> git add *
# 查看 git 工作区状态
> git status
# 从暂存区取消暂存
> git reset HEAD <file_path>
# 撤销工作区修改
> checkout -- <file_path>
# 创建提交
> git commit -m "xxx"
# 创建 amend 提交
> git commit --amend -m "xxx"
# rebase 将指定分支上的修改应用到当前分支上
> git rebase
# merge 将指定分支上的修改应用到当前分支上
> git merge
```
## 其它操作
```
# 暂存
> git stash
> git stash list
> git stash pop
# tag 标签
> git tag <tag_name>
> git tag -l
> git tag -d <tag_name>
> git push origin <tag_name>
> git push --tags
> git push origin :refs/tags/<tag_name>
> git push origin --delete tag <tag_name>
# 查看提交日志
> git log
> git log --author=bob
# 图形化工具
> git gui
> gitk [--all]
```
## 目录结构
.git 目录
```
config => 仓库配置文件
description => 仓库描述信息
HEAD => 当前分支最近一次提交的引用
FETCH_HEAD => 最近一次 Fetch 操作末端提交的引用
ORIG_HEAD => merge 和 rebase 上一个版本的 HEAD
COMMIT_EDITMSG => 最近一次提交信息
index => 暂存区内容
logs 目录 => 日志记录
refs 目录 => 本地分支、标签、远程等对象的指针
info 目录 => 仓库一些信息，全局性排除文件等
objects 目录 => 用于对象存储
hooks 目录 => 存放钩子脚本
```
.gitignore 文件
> 规则从上到下进行匹配，如前面规则匹配范围过大，后面规则会受到影响
```
/ 开头表示根目录
/ 结尾表示任意一级目录
* 通配多个字符，** 通配任意中间目录
? 通配一个字符
[] 通配一个方括号中的字符
! 开头表示不忽略匹配到的文件或目录，注意如果文件父目录被排除了，那文件规则不会生效
# 开头表示注释
```
## 高级操作（慎用）
```
# 回退
> git reset --hard HEAD^
> git reset --hard HEAD~5
> git reset --hard 3628164
# 强制推送
> git push --force
# 将另一个分支上的指定提交应用到当前分支
> git cherry-pick 5ff198e
# 删除远程分支
> git push origin :feature_xxx
```

# GitLab 常用操作
## 权限管理
* 角色权限
## 分支管理
* 基本操作
## Merge Request
* 基本操作

# VS 常用操作
## 更改
* 如何提交部分修改文件
## 分支
* 签出 => checkout
* 合并自 => merge
* 变基自 => rebase
## 同步
* 提取 => fetch
* 拉取 => pull
* 推送 => push
## 标识
* 如何删除远程 tag
## 设置
* 用户名、邮件配置

# 开发规范
## 提交信息推荐格式
* 实现xxx功能
* 修复xxx问题 / 修复bug#10021
* 优化xxx代码
* 配置xxx配置项
## 开发分支策略
* 为了减少冲突，在修改代码以及推送代码到远程前，先从服务端拉取代码，如有冲突本地解决后再推送到远程；
* 各 feature 分支，如 master 代码有更新，尽可能多地合并 master 到当前分支（直接通过 VS 合并 master 到当前分支即可），以获得最新代码；
* 不允许直接在 master 分支上修改代码，当 feature 还未发布到 PRD 环境时，所有修改均在 feature 分支上进行，需合到 master 时，通过 gitlab 发起 merge request，由相关有权限的同事来处理合并代码到 master；
* 发布 PRD 后需基于 master 打 tag，命名规则为：'v'yyyyMMddHHmm，如：v201807182120，同时还需合并 master 到 hotfix 分支，以便于后续有线上问题，在 hotfix 上修改，hotfix 发布后，需合并回 master；

# 常用三方工具
* GitHub Desktop
* Source tree

# 相关学习资料
> [Git 官方教程](https://git-scm.com/book/zh/v2)
> [跟我一起学Git](http://cs-cjl.com/2014/02/18/learn_git_with_me_01)
> 
