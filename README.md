# Avatar-BlockRestorer                
## project structure
```                    
avatar-blockrestoration/
    README.md
    mine.xlsx
    build.gradle
    LICENSE.txt
    gradlew
    changelog.txt
    settings.gradle
    CREDITS.txt
    gradle.properties
    gradlew.bat
    src/
        main/
            java/
                com/
                    avatar/
                        avatar_blockrestoration/
                            GlobalConfig.java
                            Main.java
                            server/
                                Events.java
                                ServerConfig.java
                            animation/
                                Animate.java
                            function/
                                BlockRestorer.java
            resources/
                pack.mcmeta
                META-INF/
                    mods.toml
    gradle/
        wrapper/
            gradle-wrapper.jar
            gradle-wrapper.properties                
```
## Propósito e Descrição do Projeto

Este projeto é um mod para Minecraft Forge que restaura blocos quebrados em uma área definida em torno de um bloco principal.  Ele usa um arquivo de configuração para definir os blocos e suas posições, permitindo personalização.  O mod acompanha as mudanças de blocos, salva seus estados para restauração posterior e inclui animações visuais.

## Dependências

* Forge MDK (Minecraft Development Kit)
* Java Development Kit (JDK)
* Bibliotecas necessárias do Forge (especificadas no `build.gradle` ou equivalente)

## Como Instalar

1. Clonar o repositório.
2. Instalar as dependências usando um gerenciador de pacotes (ex: Gradle).
3. Criar um arquivo de configuração (`config.properties` ou similar).
4. Executar o mod no Minecraft.

## Como Usar

Configurar o bloco principal e o raio de ação no arquivo de configuração. O mod monitora automaticamente a área e restaura blocos quebrados.  Animações indicam a destruição e restauração de blocos.

## Arquitetura

O mod é baseado em eventos do Forge.  As classes principais incluem: `Main.java` (ponto de entrada), `GlobalConfig.java` e `ServerConfig.java` (configurações), `Events.java` (manipulação de eventos), `BlockRestorer.java` (lógica de restauração) e `Animate.java` (efeitos visuais).

## Pipeline

1.  Inicialização: Carrega configurações e dados salvos.
2.  Monitoramento: Monitora mudanças de blocos.
3.  Atualização: Atualiza dados internos.
4.  Persistência: Salva dados periodicamente.
5.  Restauração: Restaura blocos quebrados sob demanda.
6.  Animação: Executa animações visuais.
                
                