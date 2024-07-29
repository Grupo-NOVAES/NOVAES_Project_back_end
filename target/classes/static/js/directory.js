const folderItems = document.getElementsByClassName("folderItem");
const fileItems = document.getElementsByClassName("fileItem");

for (let i = 0; i < folderItems.length; i++) {
    folderItems[i].addEventListener('dblclick', function() {
        const directoryId = this.getAttribute("data-id");
        window.location.href = "/directory/" + directoryId;
    });
}

for(let i=0;i < fileItems.length; i++){
    fileItems[i].addEventListener('dblclick',function() {
        console.log("teste...");
        const fileId = this.getAttribute("data-file-id");
        window.location.href = "/archive/download/"+fileId;
    })
}



document.addEventListener('DOMContentLoaded', function () {
    const folderItems = document.getElementsByClassName("folderItem");

    for (let i = 0; i < folderItems.length; i++) {
        folderItems[i].addEventListener('dblclick', function() {
            const directoryId = this.getAttribute("data-id");
            window.location.href = "/directory/" + directoryId;
        });

        folderItems[i].addEventListener('contextmenu', function(event) {
            event.preventDefault(); // Previne o menu de contexto padrão do navegador
            const contextMenu = document.getElementById('context-menu');
            
            // Posiciona o menu de contexto
            contextMenu.style.top = `${event.clientY}px`;
            contextMenu.style.left = `${event.clientX}px`;
            contextMenu.style.display = 'block';
            
            // Armazena o ID do item clicado
            contextMenu.setAttribute('data-id', this.getAttribute("data-id"));
        });
    }

    // Fecha o menu de contexto quando clicar fora
    document.addEventListener('click', function(event) {
        const contextMenu = document.getElementById('context-menu');
        if (!contextMenu.contains(event.target)) {
            contextMenu.style.display = 'none';
        }
    });

    // Alterna o tema
    const toggleThemeButton = document.getElementById("theme-checkbox");
    const body = document.body;
    const mainElement = document.querySelector('main');
    const headerElement = document.querySelector('header');
    const navElement = document.querySelector('nav');


});





function showModal() {
    document.getElementById('AddFolderModal').style.display = 'block';
}

function hideModalAddFolder() {
    document.getElementById('AddFolderModal').style.display = 'none';
}

function saveButtonAddFolder() {
    console.log('Salvar Pasta');
    hideModal();
}



function showModalFile() {
    document.getElementById('AddFolderModalFile').style.display = 'block';
}

function hideModalFile() {
    document.getElementById('AddFolderModalFile').style.display = 'none';
}

function saveButtonFile() {
    console.log('Salvar arquivo');
    hideModalFile();
}



function showModalFolderName() {
    document.getElementById('EditModalFolder').style.display = "block";
}

function hideModalEdit() {
    document.getElementById('EditModalFolder').style.display = "none";
}

function saveButtonEditFolder () {
    console.log('Salvar Edição Pasta!')
    hideModalEdit();
}