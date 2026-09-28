# PrazoFlow

Sistema de gestão de demandas de documentação técnica, com alertas automáticos via Discord.

## O problema

Como Analista de Suporte, uma das minhas responsabilidades é avaliar chamados resolvidos pela equipe e, quando a solução adotada merece virar conhecimento formal, transformar aquele chamado em uma tarefa de documentação — um manual técnico, atribuído a um responsável, com prazo de entrega. Hoje esse acompanhamento é manual, e prazos acabam esquecidos no meio da rotina de suporte.

O **PrazoFlow** nasceu para resolver esse problema no meu próprio setor, e também como projeto de estudo — aplicando na prática conceitos de Java e Spring que venho aprendendo.

> ⚠️ Este é um projeto de iniciativa pessoal, usado de forma real no meu setor de trabalho, mas **não é uma ferramenta homologada ou disponibilizada oficialmente pela empresa**.

## Como funciona

1. Pelo painel web (autenticado), o responsável cadastra uma demanda: título, descrição, prazo de entrega, técnico responsável e observações.
2. Ao vincular ou remover um técnico de uma demanda, ele recebe uma DM no Discord na hora, informando o prazo e quantos dias faltam.
3. O sistema verifica diariamente as demandas pendentes. Quando uma demanda está a 2 dias do prazo, o técnico responsável recebe um alerta privado (DM) no Discord — agrupando todas as demandas dele numa única notificação, nunca misturando com as de outro técnico.
4. Ao concluir o manual, o responsável marca a demanda como concluída no painel e o técnico recebe uma mensagem de agradecimento.
5. Se o prazo for alterado, o técnico é notificado sobre a nova data, e o ciclo de alerta reinicia a partir dela.
6. Se o prazo for ultrapassado sem conclusão, a demanda muda automaticamente para status **ATRASADO** e o técnico recebe um aviso único de atraso.
7. Se uma demanda for cancelada, o técnico responsável também é avisado.
8. O painel mostra o resultado de cada notificação enviada e permite reenviá-la manualmente a qualquer momento (a mensagem reenviada respeita o status atual da demanda).

## Stack

- **Java 21 LTS** (Eclipse Temurin)
- **Spring Boot 3.x** (Web, Data JPA, Security, Scheduling, Validation)
- **JDA 6.x** — integração com a API do Discord (WebSocket Gateway)
- **H2** (desenvolvimento) / **PostgreSQL** (produção)
- **Thymeleaf + Bootstrap 5** — painel web
- **Maven**

## Status atual do projeto

✅ **v1.0.0** — escopo inicial completo e em uso real.

- [x] Modelagem de entidades (`Tecnico`, `Demanda`, `Usuario`)
- [x] Camada de repositórios (Spring Data JPA)
- [x] Camada de serviços — regras de negócio e validações
- [x] Painel web autenticado (Thymeleaf + Bootstrap) — CRUD de demandas e técnicos
- [x] API REST (controllers + DTOs)
- [x] Autenticação e autorização (Spring Security)
- [x] Bot Discord (JDA) — envio de DMs individuais e privadas
- [x] Job agendado de verificação diária de prazos e atrasos
- [x] Notificações de vínculo, desvínculo, conclusão, cancelamento e reagendamento

## Rodando o projeto localmente

**Pré-requisitos:** Java 21, Maven e um bot Discord já criado (com o token em mãos).

```bash
git clone https://github.com/LeoLBS/prazoflow.git
cd prazoflow
```

Crie o arquivo `src/main/resources/application-local.properties` (ignorado pelo Git) com:

```properties
server.port=8085
discord.token=SEU_TOKEN_AQUI
```

E rode a aplicação com a variável de ambiente `SPRING_PROFILES_ACTIVE=local` ativa (na sua IDE, configure isso na Run Configuration do projeto; via linha de comando, exporte a variável antes de rodar `./mvnw spring-boot:run`).

A aplicação sobe na porta `8085` (ou a que você definir). O banco H2 roda em memória — nenhuma configuração adicional de banco é necessária em desenvolvimento; os dados são reiniciados a cada execução.

## Estrutura de pacotes

```
br.com.leperber.prazoflow
├── entity      Entidades JPA (Tecnico, Demanda, Usuario, ResultadoNotificacao)
├── repository  Repositórios Spring Data JPA
├── service     Regras de negócio (demandas, técnicos, notificações, alertas)
├── controller  API REST
├── web         Painel web (controllers Thymeleaf) e DTOs de formulário
├── dto         Objetos de transferência de dados da API REST
├── bot         Integração com a API do Discord (JDA)
├── config      Configurações do Spring (Security, etc.)
├── validation  Validações customizadas (ex.: dia útil)
├── util        Utilitários
└── exception   Exceções customizadas e tratamento global de erros
```

## Autor

Desenvolvido por [Leonardo Perin de Berso](https://github.com/LeoLBS) como projeto pessoal de estudo e uso real no setor de suporte técnico.
