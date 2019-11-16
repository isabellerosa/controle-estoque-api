# Controle de Estoque RESTful API :) 

> Api de controle de estoque que gerencia lojas e produtos. 

## Sem enrolação, vai direto para o que te interessa:
- [Instalação](#Instalação)
- [Configuração](#Configuração)
- [Documentação](#Documentação)
- [Tarefas](#Tarefas)
- [Melhorar](#Melhorar)

---
## Instalação 
- Tem Docker e um Makefile para deixar ainda mais simples configurar.

- Clone este repositório na sua máquina `https://github.com/isabellerosa/desafio-controle-estoque.git`

- Isso é tudo pessoal.

## Configuração
> Apesar de o projeto estar completamente Dockerizado, você pode optar por não utilizar docker :(

> Gere o `.jar`
```
$ mvn install -DskipTests
```
> Rode o comando usando suas credenciais do banco
```
$ java -Dspring.profiles.active=prod -DDB_DRIVER=com.mysql.jdbc.Driver -DDB_URL="jdbc:mysql://dbaddress:port/dbname" -DDB_USER=user -DDB_PASSWORD=password -DDB_DIALECT=org.hibernate.dialect.MySQL5InnoDBDialect -DDB_DDL=update -jar inventory-control-0.0.1-SNAPSHOT.jar
```
___

> Caso queira usar um banco na sua máquina, basta editar o arquivo ```inventory.env``` e adicionar os dados de conexão do seu banco local:

```
MYSQL_ROOT_PASSWORD= Senha de ROOT Mysql
MYSQL_DATABASE= Nome do Banco 


ACTIVE_PROFILE= Qual profile usar? (dev ou prod)
DB_USER= Seu usuário
DB_PASSWORD= Senha
DB_URL= URL
DB_DRIVER= Driver
DB_DIALECT= Dialect
DB_DDL= (create, update, validate ou create-drop)
```

:exclamation: Existe um docker compose no projeto, não é necessário alterar este arquivo para rodar a aplicação. 

### Linux
> Entre na pasta que você acabou de clonar e dê um make, :
```
$ make
```

:exclamation: Existe um ```help``` como default no ```Makefile```, vai mostrar todas as opções disponíveis.


### Windows
> Basta rodar o compose:

```
$ docker-compose up
```

:exclamation: Lembre-se que este comando irá utilizar o banco SQL do docker, caso queira usar seu banco local leia o início desta seção - [Configuração](#Configuração)

> O projeto também tem H2 configurado, é um banco de dados em memória, caso queira utilizado basta alterar no arquivo ```inventory.env``` o ```ACTIVE_PROFILE``` para ```dev```:

```fix
ACTIVE_PROFILE=dev
```

## Documentação

- Todos os endpoints e suas respectivas respostas

## Criar Loja

### Request

`POST /user/:userId/store`
```
{
    "name":"Loja"
}
```

### Response
Status: 201 Created

```json
{
    "name": "Loja",
    "id": "BSuQqS29"
}
```

## Listar Lojas

### Request

`GET /user/:userId/store`

### Response
Status: 200 OK

```json
[
    {
        "name": "Loja 1",
        "id": "HtyFjPMZ"
    },
    {
        "name": "Loja 2",
        "id": "BSuQqS29"
    },
    {
        "name": "Loja 3",
        "id": "TcLMHup4"
    }
]
```

## Atualizar Loja

### Request

`PUT /user/:userId/store/:storeId`

```json
{
	"name":"Loja Atualizada"
}
```


### Response
Status: 200 OK

```json
{
    "name": "sdf",
    "id": "TcLMHup4"
}

```


## Deletar Loja

### Request

`DELETE /user/:userId/store/:storeId`


### Response
Status: 200 OK

```json
{
    "name": "Loja",
    "id": "HtyFjPMZ"
}
```

## Criar Item

### Request

`POST /user/:userId/item`

```json
{
	"name": "Produto",
	"price": 52.00
}
```

### Response
Status: 201 Created

```json
{
    "name": "Produto",
    "price": 52.00,
    "id": "AL80fR4W"
}
```

## Listar Items

### Request

`GET /user/:userId/item`

### Response
Status: 200 OK

```json
[
    {
        "name": "Produto 1",
        "price": 34.00,
        "id": "fjDdwisg"
    },
    {
        "name": "Produto 2",
        "price": 212.23,
        "id": "HtyFjPMZ"
    }
]
```

## Atualizar Item

### Request

`PUT /user/:userId/item/:itemId`

```json
{
	"name": "Produto Atualizado",
	"price": 1254.65
}
```

### Response
Status: 200 OK

```json
{
    "name": "Produto Atualizado",
	"price": 1254.65,
    "id": "fjDdwisg"
}
```

## Deletar Item

### Request

`DELETE /user/:userId/item/:itemId`

### Response
Status: 200 OK

```json
{
    "name": "Produto",
    "price": 212.23,
    "id": "fjDdwisg"
}
```

## Adicionar Item ao Estoque

### Request

`POST /user/:userId/store/:storeId/stock`

```json
{
	"item": "BFd4Hfew",
	"quantity": 34
}
```

### Response
Status: 201 Created

```json
{
    "item": {
        "name": "Produto 2",
        "price": 25.00,
        "id": "BFd4Hfew"
    },
    "quantity": 34
}
```

## Ver Items no Estoque

### Request

`GET /user/:userId/store/:storeId/stock/`

### Response
Status: 200 OK

```json
{
    "store": {
        "name": "Loja 1",
        "id": "12k3j4kj"
    },
    "items": [
        {
            "item": {
                "name": "Produto 2",
                "price": 25.00,
                "id": "BFd4Hfew"
            },
            "quantity": 3
        },
        {
            "item": {
                "name": "Produto 1",
                "price": 15.78,
                "id": "ASad4JAK"
            },
            "quantity": 150
        }
    ]
}
```

## Atualizar Item em Estoque

### Request

`PUT /user/:userId/store/:storeId/stock/:itemId`

```json
{
	"quantity": 66
}
```

### Response
Status: 200 OK

```json
{
    "item": {
        "name": "Produto",
        "price": 25.00,
        "id": "BFd4Hfew"
    },
    "quantity": 66
}
```

## Deletar item do Estoque

### Request

`DELETE /user/:userId/store/:storeId/stock/:itemId`

### Response
Status: 200 OK

```json
{
    "item": {
        "name": "Produto",
        "price": 25.00,
        "id": "BFd4Hfew"
    },
    "quantity": 3
}
```

## Tarefas 
- [:heavy_check_mark:] Criar, editar e excluir uma ou mais lojas 
- [:heavy_check_mark:] Criar, editar e excluir itens
- [:heavy_check_mark:] Adicionar, editar e remover itens do estoque das lojas.
- [:heavy_check_mark:] Listar itens e listar lojas

## Melhorar
- [:heavy_check_mark:] Ter profiles para dev e prod para facilitar o desenvolvimento.
- [:heavy_check_mark:] Adicionar H2 para conseguir usar o ambiente de dev com ainda mais facilidade.
- [:heavy_check_mark:] Dockerizar completamente a aplicação
- [:heavy_check_mark:] Criar um Makefile para simplificar a utilização no linux
- [:heavy_multiplication_x:] Criar endpoints que aceitem batch
- [:heavy_multiplication_x:] Aceitar filtros e ordenação nos endpoints de GET
- [:heavy_multiplication_x:] Criar testes unitários com JUNIT + Mockito para todos os métodos
- [:heavy_multiplication_x:] Melhorar respostas de erro nos endpoints e retornar mensagens mais instrutivas nos erros
- [:heavy_multiplication_x:] Criar testes de integração
- [:heavy_multiplication_x:] Melhorar a documentação com uso de Swagger ou algum serviço externo. No momento toda documentação está no README, o que não é tão legal
- [:heavy_multiplication_x:] Usar DTO para um projeto deste porte foi overkill, remover e trabalhar apenas com os objetos de request e response
- [:heavy_multiplication_x:] Adicionar Spring Security e endpoint com autenticação JWT, incluindo um outro endpoint para token refresh
- [:heavy_multiplication_x:] Configurar uma pipe de CI para rodar os testes unitários
