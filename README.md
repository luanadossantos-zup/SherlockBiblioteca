# Biblioteca Sherlock
## 🕵️‍♂️ 1. Objetivo do Projeto
Este projeto consiste em um aplicativo Android nativo para navegação em uma biblioteca fictícia de livros de Sherlock Holmes. 
O app foi desenvolvido com o objetivo de praticar a arquitetura MVVM, programação reativa com Kotlin Flow, e o uso de componentes como RecyclerView, ViewModel e Repository.

## ✅ 2. Funcionalidades
- Busca reativa por nome do livro
- Filtro por tipo de obra (Conto ou Romance)
- Exibição paginada com scroll infinito
- Leitura de dados a partir de um arquivo JSON
- Separação em camadas MVVM
- Interface responsiva com ViewBinding

## 🧱 3. Arquitetura Utilizada (MVVM)
- View (Activity): Interage com o usuário e observa os dados.
- ViewModel: Contém a lógica de apresentação, estado e regras de negócio.
- Model/Repository: Responsável por fornecer os dados (no caso, via arquivo local JSON).

## 🧩 4. Explicação das principais classes
- `MainActivity`: Responsável por configurar a interface, capturar interações e observar os dados vindos da ViewModel.
- `MainViewModel`: Gerencia os dados visíveis, aplica filtros, controla paginação e responde à busca digitada.
- `BookRepository`: Lê os dados do arquivo livros.json e os transforma em objetos.
- `BookAdapter`: Exibe cada livro na RecyclerView com eficiência usando DiffUtil.

## 🔄 5. Fluxo de dados
1. A Activity envia a busca e os filtros para a ViewModel
2. A ViewModel filtra os livros e emite uma lista paginada via StateFlow
3. A Activity observa esse fluxo e atualiza a RecyclerView com o novo conteúdo
4. O Repository fornece os dados do arquivo local `livros.json`

## 🧪 6. Como rodar o projeto
1. Abra o projeto no Android Studio
2. Tenha a pasta assets com o arquivo `livros.json`
3. Compile e execute no emulador ou dispositivo físico (minSdk = 24)
