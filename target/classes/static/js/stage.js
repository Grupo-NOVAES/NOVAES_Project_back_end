let selectedContractId = null;
let modalVisible = false;



function showModalActionsStage(button) {
    const ModalOptions = document.getElementById('ModalOptions');
    selectedContractId = button.closest('tr').getAttribute('data-id');

    const rect = button.getBoundingClientRect();
    const modalWidth = ModalOptions.offsetWidth;
    const modalHeight = ModalOptions.offsetHeight;

    const marginLeft = 150;

    let modalLeft = rect.left - modalWidth - marginLeft;
    let modalTop = rect.top + window.scrollY;


    ModalOptions.style.top = `${modalTop}px`;
    ModalOptions.style.left = `${modalLeft}px`;

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


document.querySelector('.input').addEventListener('input', function () {
    const searchTerm = this.value.toLowerCase();
    const stages = document.querySelectorAll('#stages');

    stages.forEach(function (stage) {
        const stageName = stage.getAttribute('data-name').toLowerCase();

        if (stageName.includes(searchTerm)) {
            stage.style.display = '';
        } else {
            stage.style.display = 'none';
        }
    });
});
