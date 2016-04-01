# Livraria - Treinamento de AngularJS + Demoiselle 2.5

A aplicação tem por objetivo auxiliar no treinamento de AngularJS + Demoiselle 2.5 ministrado pela CETEC/CTCTA.

Ela é dividia em duas pastas

### backend

> Essa pasta armazena o código fonte da aplicação backend criada em Demoiselle 2.5 que disponibiliza os serviços REST para a aplicação AngularJS consumir.

### frontend

> Essa pasta armazena o código fonte da aplicação AngularJS

## Instalação

Para a instalação do **frontend** você precisá executar os seguintes passos em um terminal:

```sh
$ cd <pasta de instalação>/frontend
$ sudo apt-get install nodejs
$ sudo npm install bower -g
$ sudo npm install grunt-cli -g 
$ rm -r -f ~/.npm/_locks/
$ git config --global url."https://".insteadOf git://
$ npm install
$ bower install
```

## Execução do AngularJS com GruntJS

### Ambiente de desenvolvimento

```sh
$ cd <pasta de instalação>/frontend
$ grunt serve
```

Acesse a url http://localhost:9000

### Distribuição da aplicação

```sh
$ cd <pasta de instalação>/frontend
$ grunt build
```
A pasta **dist** será criada como produto do comando anterior.

## Roteiro

### Etapa 1
- Apresentação da estrutra de pastas
  - app
    - images
    - scripts
      - controllers
      - directives
      - filters
      - services
    - styles
    - views
      - directives
      - partials

### Etapa 2
- Criação do CRUD de Usuários
  - Controller
  - Service
  - Importação do Controller e do Serviço do index.html
  - Criação da rota no app.module.js
  - Adicionado o link no menu para o Usuário em index.html

### Etapa 3
- Criação do CRUD de Book
  - Controller
  - Service
  - Importação do Controller e do Serviço do index.html
  - Criação da rota no app.module.js
  - Adicionado o link no menu para o Livro em index.html
- Instalação dos componentes **ng-file-upload-shim**, **ng-file-upload** e **angular-file-saver** para funcionalidade de Upload e Dowload
- Configuração dos novos componentes no arquivo app.module.js

```sh
$ cd <pasta de instalação>/frontend
$ bower install ng-file-upload-shim --save
$ bower install ng-file-upload --save
$ bower angular-file-saver --save
```  