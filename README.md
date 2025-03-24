# 🧠 Kanban Board System - Avanade DecolaTech 2025

Este é um sistema de gerenciamento de boards Kanban desenvolvido como parte do programa **Avanade DecolaTech 2025**. Ele permite aos usuários criarem e gerenciarem boards, colunas e cards de maneira interativa via console.

---

## 🚀 Tecnologias Utilizadas

- **Java 11+**
- **JDBC** (para conexão com banco de dados)
- **Lombok** (para redução de _boilerplate code_)
- **SQL** (para persistência dos dados)
- **Scanner** (para entrada de dados no console)
- **Maven** (para gerenciamento de dependências e execução)

---

## 📋 Funcionalidades

O sistema oferece as seguintes funcionalidades principais:

- ✅ Criar um novo **board** com colunas padrão ou personalizadas
- 🔍 Selecionar um board existente pelo ID
- 🗑️ Excluir um board pelo ID
- 🧩 Gerenciar **cards** (criar, mover, bloquear, desbloquear, cancelar)
- 🧭 Visualizar o board e suas colunas com os cards
- ❌ Encerrar a aplicação

---

## 🧱 Estrutura do Projeto

```
src
└── br
    └── com
        └── dio
            ├── dto
            │   └── BoardColumnInfoDTO.java
            ├── persistence
            │   ├── entity
            │   │   ├── BoardEntity.java
            │   │   ├── BoardColumnEntity.java
            │   │   └── CardEntity.java
            │   └── config
            │       └── ConnectionConfig.java
            ├── service
            │   ├── BoardColumnQueryService.java
            │   ├── BoardQueryService.java
            │   ├── CardQueryService.java
            │   └── CardService.java
            └── ui
                ├── MainMenu.java
                └── BoardMenu.java
```

---

## 🔑 Principais Classes

- **MainMenu**: Interação inicial com o usuário (criar, excluir ou selecionar board)
- **BoardMenu**: Gerencia os cards dentro do board selecionado
- **CardService**: Operações com cards (criar, mover, bloquear, etc)
- **BoardQueryService / BoardColumnQueryService**: Consultas de dados
- **BoardEntity / BoardColumnEntity / CardEntity**: Representações das tabelas do banco
- **ConnectionConfig**: Configurações de conexão JDBC com o banco de dados

---

## 🛠️ Como Rodar o Projeto

### 1. Clonar o Repositório

```bash
git clone https://github.com/seuusuario/kanban-board.git
cd kanban-board
```

### 2. Configurar o Banco de Dados

Configure o banco de dados com as tabelas necessárias. Edite a classe `ConnectionConfig.java` com suas credenciais:

```java
String url = "jdbc:mysql://localhost:3306/seubanco";
String user = "usuario";
String password = "senha";
```

> ⚠️ Certifique-se de que as tabelas estejam criadas conforme o modelo esperado pelo sistema.

### 3. Executar o Projeto

```bash
mvn clean install
mvn exec:java
```

### 4. Interagir com o Sistema

Ao iniciar o programa, o menu principal será exibido:

```
Bem-vindo ao gerenciador de boards, escolha a opção desejada:
1 - Criar um novo board
2 - Selecionar um board existente
3 - Excluir um board
4 - Sair
```

---

## 📦 Dependências

As dependências estão configuradas no arquivo `pom.xml`:

- [Lombok](https://projectlombok.org/)
- JDBC Driver (ex: MySQL)
- Maven Plugins

---

## 🤝 Contribuição

Este projeto foi desenvolvido como parte do programa **Avanade DecolaTech 2025**, com foco em aprendizado prático de boas práticas em Java, SQL e arquitetura de sistemas.

Pull Requests e sugestões são bem-vindas!

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## ✨ Feito com dedicação no DecolaTech 🚀
