let currentDirectoryId = null;
let currentArchiveId = null;
let selectedUserId = null;
let modalVisible = false;
let selectedFileId = null;
let File = false;

// Função para fechar todos os modais
function hideAllModals() {
    document.getElementById('AddFolderModalFile').style.display = 'none';
    document.getElementById('ModalOptions').style.display = 'none';
    document.getElementById('ModalOptionsFile').style.display = 'none';
    modalVisible = false;
    File = false;
}

document.addEventListener('click', function(event) {
    const contextmenuDirectory = document.getElementById('ModalOptions');
    const contextmenuFile = document.getElementById('ModalOptionsFile');
    const target = event.target;

    if (modalVisible && contextmenuDirectory && !contextmenuDirectory.contains(target) && !target.closest('button')) {
        hideAllModals();
    }

    if (File && contextmenuFile && !contextmenuFile.contains(target) && !target.closest('button')) {
        hideAllModals();
    }
});

function showModalActionsDirectory(button) {
    const contextmenu = document.getElementById('ModalOptions');
    selectedUserId = button.closest('tr').getAttribute('data-id');

    const rect = button.getBoundingClientRect();
    contextmenu.style.top = `${rect.top + window.scrollY}px`;
    contextmenu.style.left = `${rect.left + window.scrollX}px`;

    contextmenu.style.display = 'block';
    modalVisible = true;
    currentDirectoryId = selectedUserId;
    console.log(selectedUserId);
    console.log(currentDirectoryId);
}

function showModalFile() {
    document.getElementById('AddFolderModalFile').style.display = 'block';
}

function hideModalFile() {
    document.getElementById('AddFolderModalFile').style.display = 'none';
}

function showModalActionsFiles(button) {
    const contextmenu = document.getElementById('ModalOptionsFile');
    selectedFileId = button.closest('tr').getAttribute('data-file-id');

    const rect = button.getBoundingClientRect();
    contextmenu.style.top = `${rect.top + window.scrollY}px`;
    contextmenu.style.left = `${rect.left + window.scrollX}px`;

    contextmenu.style.display = 'block';
    File = true;
    currentArchiveId = selectedFileId;
    console.log(selectedFileId);
}

function saveButtonAddFolder() {
    console.log('Salvar Pasta');
    let id = document.getElementById("inputParentDirectoryId").value;
    console.log(id);
    hideAllModals();
}

function saveButtonFile() {
    console.log('Salvar arquivo');
    hideAllModals();
}

function saveButtonEditFolder() {
    console.log('Salvar Edição Pasta!');
    hideAllModals();
}

function saveButtonEditFile() {
    console.log('Salvar Edição Pasta!');
    hideAllModals();
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
    hideAllModals();
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
    hideAllModals();
}
