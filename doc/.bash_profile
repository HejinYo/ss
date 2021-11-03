
# HomeBrew
export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.cloud.tencent.com/homebrew-bottles
export PATH="/usr/local/bin:$PATH"
export PATH="/usr/local/sbin:$PATH"
# HomeBrew END


# HomeBrew
export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.ustc.edu.cn/homebrew-bottles
export PATH="/usr/local/bin:$PATH"
export PATH="/usr/local/sbin:$PATH"
# HomeBrew END


# JavaHome
export JAVA_8_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home
export JAVA_16_HOME=/Library/Java/JavaVirtualMachines/jdk-16.0.1.jdk/Contents/Home
# JavaHome END

export JAVA_HOME=$JAVA_8_HOME
alias jdk8="export JAVA_HOME=$JAVA_8_HOME"
alias jdk16="export JAVA_HOME=$JAVA_16_HOME"

# MavenHome
export MAVEN_HOME=/Users/hejinyo/java/tools/maven
export PATH=$PATH:$MAVEN_HOME/bin
# MavenHome END

# Jmeter
export JMETER_HOME=/Users/hejinyo/java/tools/jmeter
export PATH=$PATH:$JMETER_HOME/bin
# Jmeter END

# proxy
function proxy_off(){
    unset http_prox
    unset https_proxy
    echo -e "已关闭代理"
}

function proxy_on() {
    /usr/local/sbin/privoxy /usr/local/etc/privoxy/config
    export no_proxy="localhost,127.0.0.1,localaddress,.localdomain.com"
    export http_proxy="http://127.0.0.1:8118"
    export https_proxy=$http_proxy
    echo -e "已开启代理"
}

function sp(){
    source ~/.zshrc
}
# proxy END

alias ll='ls -l'
alias la='ls -a'

export CLICOLOR=1
export LSCOLORS=gxfxaxdxcxegedabagacad
