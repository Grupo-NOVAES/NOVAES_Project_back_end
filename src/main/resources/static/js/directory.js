let currentDirectoryId = null;
let currentArchiveId = null;
let selectedUserId = null;
let modalVisible = false;
let File = false;

document.addEventListener('DOMContentLoaded', function () {
    const folderItems = document.getElementsByClassName("folderItem");
    const fileItems = document.getElementsByClassName("fileItem");

    for (let i = 0; i < folderItems.length; i++) {
        folderItems[i].addEventListener('dblclick', function() {
            const directoryId = this.getAttribute("data-id");
            window.location.href = "/directory/" + directoryId;
        });    
    }
    for(let i=0; i < fileItems.length; i++){
        fileItems[i].addEventListener('dblclick', function() {
            const fileId = this.getAttribute("data-file-id");
            window.location.href = "/archive/download/"+fileId;
        });
    }
});

function showModalFolder() {
    document.getElementById('AddFolderModal').style.display = 'block';
}

function hideModalAddFolder() {
    document.getElementById('AddFolderModal').style.display = 'none';
}

function saveButtonAddFolder() {
    console.log('Salvar Pasta');
    let id = document.getElementById("inputParentDirectoryId").value;
    console.log(id);
    hideModalAddFolder();
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

function saveButtonEditFolder() {
    console.log('Salvar Edição Pasta!');
    hideModalEdit();
}

function showModalFileName() {
    document.getElementById('EditModalFile').style.display = "block";
}

function hideModalEditFile() {
    document.getElementById('EditModalFile').style.display = "none";
}

function saveButtonEditFile() {
    console.log('Salvar Edição Pasta!');
    hideModalEditFile();
}

function hideAllModal() {
    document.getElementById('EditModalFile').style.display = "none";
    document.getElementById('AddFolderModalFile').style.display = 'none';
}

document.getElementById('searchInput').addEventListener('input', function() {
    let filter = this.value.toLowerCase();
    let rows = document.querySelectorAll('#directoryTable tbody tr');
    rows.forEach(row => {
        let text = row.textContent.toLowerCase();
        if (text.includes(filter)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
});

document.addEventListener('click', function(event) {
    const directoryContextMenu = document.getElementById('ModalOptions');
    const fileContextMenu = document.getElementById('ModalOptionsFile');
    const target = event.target;

    if (modalVisible && !directoryContextMenu.contains(target) && !target.closest('button')) {
        hideModalActionsDirectory();
    }

    if (File && !fileContextMenu.contains(target) && !target.closest('button')) {
        hideModalActionsFiles();
    }
});

function showModalActionsDirectory(button) {
    const contextmenu = document.getElementById('ModalOptions');
    selectedUserId = button.closest('tr').getAttribute('data-id');

    const rect = button.getBoundingClientRect();

    const marginLeft = 125;
    const marginTop = -40;

    let modalLeft = rect.left - marginLeft;
    let modalTop = rect.top + marginTop;


    contextmenu.style.top = `${modalTop}px`;
    contextmenu.style.left = `${modalLeft}px`;

    contextmenu.style.display = 'block';
    modalVisible = true;   
    currentDirectoryId = selectedUserId;
    document.getElementById("directoryId").value = currentDirectoryId;
}

function hideModalActionsDirectory() {
    const contextmenu = document.getElementById('ModalOptions');
    contextmenu.style.display = 'none';
    modalVisible = false;
}

function downloadDirectory() {
    window.location.href = `/directory/download/${currentDirectoryId}`;
}

function downloadFile() {
    window.location.href = `/archive/download/${currentArchiveId}`;
}

function showModalActionsFiles(button) {
    const contextmenu = document.getElementById('ModalOptionsFile');
    selectedFileId = button.closest('tr').getAttribute('data-file-id');

    const rect = button.getBoundingClientRect();

    const marginLeft = 180;
    const marginTop = -40;

    let modalLeft = rect.left - marginLeft;
    let modalTop = rect.top + marginTop;


    contextmenu.style.top = `${modalTop}px`;
    contextmenu.style.left = `${modalLeft}px`;

    contextmenu.style.display = 'block';
    File = true;
    currentArchiveId = selectedFileId;
    document.getElementById("archiveId").value = selectedFileId;
}

function hideModalActionsFiles() {
    const contextmenu = document.getElementById('ModalOptionsFile');
    contextmenu.style.display = 'none';
    File = false;
}

async function deleteFile() {
    if (currentArchiveId === null) {
        alert('Nenhum item selecionado para exclusão.');
        return;
    }
    if (confirm('Você tem certeza que deseja excluir este Arquivo?')) {
        let response = await fetch(`/archive/delete/${currentArchiveId}`, {
            method: 'POST'
        });
        console.log(JSON.stringify(response));
    }
    window.location.reload();
    hideModalActionsFiles();
}

async function deleteFolder() {
    if (currentDirectoryId === null) {
        alert('Nenhum item selecionado para exclusão.');
        return;
    }

    if (confirm('Você tem certeza que deseja excluir esta pasta?')) {
        let response = await fetch(`/directory/delete/${currentDirectoryId}`, {
            method: 'POST', 
        });
        console.log(JSON.stringify(response));
    }
    window.location.reload();
    hideModalActionsDirectory();
}
