# PrazoFlow

Sistema de gestão de demandas de documentação técnica, com alertas automáticos via Discord.

## O problema

Como Analista de Suporte, uma das minhas responsabilidades é avaliar chamados resolvidos pela equipe e, quando a solução adotada merece virar conhecimento formal, transformar aquele chamado em uma tarefa de documentação — um manual técnico, atribuído a um responsável, com prazo de entrega. Hoje esse acompanhamento é manual, e prazos acabam esquecidos no meio da rotina de suporte.

O **PrazoFlow** nasceu para resolver esse problema no meu próprio setor, e também como projeto de estudo — aplicando na prática conceitos de Java e Spring que venho aprendendo.

> ⚠️ Este é um projeto de iniciativa pessoal, usado de forma real no meu setor de trabalho, mas **não é uma ferramenta homologada ou disponibilizada oficialmente pela empresa**.

## Como funciona

1. Pelo painel web (autenticado), o responsável cadastra uma demanda: técnico responsável, título do manual, descrição, prazo de entrega e observações.
2. O sistema verifica diariamente as demandas pendentes. Quando uma demanda está a 2 dias do prazo, o técnico responsável recebe um alerta privado (DM) no Discord — agrupando todas as demandas dele numa única notificação.
3. Ao concluir o manual dentro do prazo, o técnico recebe uma mensagem de agradecimento.
4. Se o prazo for alterado, o técnico é notificado sobre a nova data, e o ciclo de alerta reinicia.
5. Se o prazo for ultrapassado sem conclusão, a demanda muda para status **ATRASADO** e o técnico recebe um aviso único de atraso.

## Stack

- **Java 21 LTS** (Eclipse Temurin)
- **Spring Boot** (Web, Data JPA, Security, Scheduling)
- **JDA 6.x** — integração com a API do Discord (WebSocket Gateway)
- **H2** (desenvolvimento) / **PostgreSQL** (produção)
- **Thymeleaf + Bootstrap** — painel web
- **Maven**

## Status atual do projeto

🚧 Em desenvolvimento ativo.

- [x] Modelagem de entidades (`Tecnico`, `Demanda`, `Usuario`)
- [x] Camada de repositórios (Spring Data JPA)
- [x] Camada de serviços — regras de negócio e validações
- [x] Camada web (controllers + DTOs)
- [ ] Autenticação (Spring Security)
- [ ] Integração com o bot Discord (JDA)
- [ ] Job agendado de verificação de prazos

## Rodando o projeto localmente

**Pré-requisitos:** Java 21 e Maven instalados.

```bash
git clone https://github.com/LeoLBS/prazoflow.git
cd prazoflow
./mvnw spring-boot:run
```

A aplicação sobe por padrão na porta `8080`. O banco H2 roda em memória — nenhuma configuração adicional é necessária para desenvolvimento.

## Estrutura de pacotes

```
br.com.leperber.prazoflow
- entity        (Entidades JPA: Tecnico, Demanda, Usuario)
- repository    (Repositórios Spring Data JPA)
- service       (Regras de negócio)
- controller    (Endpoints REST / painel web — em desenvolvimento)
- bot           (Integração com a API do Discord — em desenvolvimento)
- dto           (Objetos de transferência de dados — em desenvolvimento)
- config        (Configurações do Spring: Security, etc.)
- exception     (Exceções customizadas)
```


## Autor

Desenvolvido por [Leonardo](https://github.com/LeoLBS) como projeto pessoal de estudo e uso real no setor de suporte técnico.