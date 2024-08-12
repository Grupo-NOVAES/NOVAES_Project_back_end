function showModalStage(){
    document.getElementById('AddModalStage').style.display = "block";
}

function hideButton() {
    document.getElementById('AddModalStage').style.display = "none";
}

function saveButton() {
    console.log('Salvar Add Modal Stage!');
}




let selectedContractId = null;
let modalVisible = false;

document.addEventListener('click', function(event) {
    const ModalOptions = document.getElementById('ModalOptions');
    const target = event.target;

    if (modalVisible && !ModalOptions.contains(target) && !target.closest('button')) {
        hideModalActionsStage();
    }
});

function showModalActionsStage(button) {
    const ModalOptions = document.getElementById('ModalOptions');
    selectedContractId = button.closest('tr').getAttribute('data-id');

    const rect = button.getBoundingClientRect();
    ModalOptions.style.top = `${rect.top + window.scrollY}px`; 
    ModalOptions.style.left = `${rect.left + window.scrollX}px`; 

    ModalOptions.style.display = 'block';
    modalVisible = true;
}

function hideModalActionsStage() {
    const ModalOptions = document.getElementById('ModalOptions');
    ModalOptions.style.display = 'none';
    modalVisible = false;
}




function showModalEditStage() {
    document.getElementById('EditModalContract').style.display = 'block';
    const contractRow = document.querySelector(`tr[data-id="${selectedContractId}"]`);
    document.getElementById('name').value = contractRow.getAttribute('data-title');
}

function hideModalEditStage() {
    document.getElementById('EditModalContract').style.display = 'none';
}

function saveButtonEditContract() {
    const newTitle = document.getElementById('name').value;
    updateStage(newTitle);
    hideModalEditStage();
    hideModalActionsStage();
}

function confirmDeleteStage() {
    if (confirm('Deseja excluir este contrato?')) {
        deleteStage();
        hideModalActionsStage();
    }
}


function confirmConcludeStage(){
    if(confirm('Deseja Concluir esta Etapa?')){
        concludeStage();
        hideModalActionsStage();
    }
}


function updateStage(){
    console.log('Etapa Atualizada')
}

function deleteStage(){
    console.log('Etapa Excluida')
}

function concludeStage(){
    console.log('Etapa concluida')
}