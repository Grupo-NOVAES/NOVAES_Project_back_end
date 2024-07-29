// Adiciona funcionalidade de duplo clique para redirecionar
const folderItems = document.getElementsByClassName("folderItem");

for (let i = 0; i < folderItems.length; i++) {
    folderItems[i].addEventListener('dblclick', function() {
        const directoryId = this.getAttribute("data-id");
        window.location.href = "/directory/" + directoryId;
    });
}

// Adiciona funcionalidade de alternância de tema
document.addEventListener('DOMContentLoaded', function () {
    const toggleThemeButton = document.getElementById("ThemeButton");
    const body = document.body;
    const mainElement = document.querySelector('main');
    const headerElement = document.querySelector('header');
    const navElement = document.querySelector('nav');

    toggleThemeButton.addEventListener("click", function () {
        body.classList.toggle("dark-theme");
        mainElement.classList.toggle("dark-theme");
        headerElement.classList.toggle("dark-theme-header");
        navElement.classList.toggle("dark-theme-nav");
    });

    // Cria e adiciona o menu contextual
    const contextMenu = document.createElement('div');
    contextMenu.className = 'context-menu';
    contextMenu.innerHTML = `
        <div class="context-menu-item" id="rename">Renomear</div>
        <div class="context-menu-item" id="delete">Deletar</div>
    `;
    document.body.appendChild(contextMenu);

    document.querySelectorAll('.folderItem').forEach(item => {
        item.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            const x = e.pageX;
            const y = e.pageY;
            contextMenu.style.left = `${x}px`;
            contextMenu.style.top = `${y}px`;
            contextMenu.style.display = 'block';
            contextMenu.dataset.itemId = item.dataset.id;
            contextMenu.dataset.itemName = item.dataset.name;
        });
    });

    document.addEventListener('click', () => {
        contextMenu.style.display = 'none';
    });

    contextMenu.querySelector('#rename').addEventListener('click', () => {
        const itemId = contextMenu.dataset.itemId;
        const itemName = contextMenu.dataset.itemName;
        alert(`Renomear item: ${itemId} - ${itemName}`);
        // Adicione a lógica para renomear o item aqui
    });

    contextMenu.querySelector('#delete').addEventListener('click', () => {
        const itemId = contextMenu.dataset.itemId;
        const itemName = contextMenu.dataset.itemName;
        if (confirm(`Tem certeza que deseja deletar ${itemName}?`)) {
            alert(`Deletar item: ${itemId}`);
            // Adicione a lógica para deletar o item aqui
        }
    });
});
