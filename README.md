# Sistema de Eventos da Cidade

Projeto em Java desenvolvido para console, seguindo a estrutura MVC, com cadastro de usuario, cadastro de eventos, confirmacao de participacao e persistencia em arquivo texto.

## Objetivo

O sistema permite:

- cadastrar um usuario para a sessao atual;
- cadastrar eventos com nome, endereco, categoria, horario, duracao e descricao;
- listar eventos ordenados por status e horario;
- identificar se um evento esta proximo, acontecendo agora ou se ja aconteceu;
- confirmar participacao em eventos disponiveis;
- visualizar eventos com presenca confirmada;
- cancelar participacao em eventos futuros ou em andamento;
- salvar e carregar eventos pelo arquivo `events.data`.

## Tecnologias

- Java
- Programacao orientada a objetos
- Arquitetura MVC
- Persistencia em arquivo texto

## Estrutura do Projeto

```text
src/
  com/
    pratique/
      events/
        App.java
        controller/
          ApplicationController.java
        model/
          Event.java
          EventCategory.java
          EventStatus.java
          User.java
        repository/
          FileEventRepository.java
        view/
          ConsoleView.java
events.data
