let selectedContractId = null;
let modalVisible = false;



function showModalActionsStage(button) {
    const ModalOptions = document.getElementById('ModalOptions');
    selectedContractId = button.closest('tr').getAttribute('data-id');

    const rect = button.getBoundingClientRect();
    ModalOptions.style.top = `${rect.top + window.scrollY}px`; 
    ModalOptions.style.left = `${rect.left + window.scrollX}px`; 

    ModalOptions.style.display = 'block';
    modalVisible = true;
    document.getElementById("idStage").value = selectedContractId;
    console.log(document.getElementById("idStage").value);
}

document.addEventListener('click', function(event) {
    const ModalOptions = document.getElementById('ModalOptions');
    const target = event.target;

    if (modalVisible && !ModalOptions.contains(target) && !target.closest('button')) {
        hideModalActionsStage();
    }
});

function hideModalActionsStage() {
    const ModalOptions = document.getElementById('ModalOptions');
    ModalOptions.style.display = 'none';
    modalVisible = false;
}
function showModalStage(){
    document.getElementById('AddModalStage').style.display = "block";
}
function hideModalStage() {
    document.getElementById('AddModalStage').style.display = "none";
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
        hideAllModal();
        
    }
}

function confirmConcludeStage(){
    if(confirm('Deseja alterar o status desta Etapa?')){
        concludeStage();
        hideModalActionsStage();
    }
}

function hideAllModal(){
    hideModalActionsStage();
    hideModalEditStage();
    hideModalStage();
}

async function deleteStage(){
   let response = await fetch(`/stages/deleteStage/${selectedContractId}`,{
    method:"POST"
   });
   console.log(JSON.stringify(response));
   window.location.reload();
}

async function concludeStage(){
    let response = await fetch(`/stages/concludeStage/${selectedContractId}`,{
        method:"POST"
    });
    window.location.reload();
}