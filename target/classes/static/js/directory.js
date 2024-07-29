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