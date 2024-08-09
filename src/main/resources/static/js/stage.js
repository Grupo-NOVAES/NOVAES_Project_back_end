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
        hideModalActionsContract();
    }
});

function showModalActionsContract(button) {
    const ModalOptions = document.getElementById('ModalOptions');
    selectedContractId = button.closest('tr').getAttribute('data-id');

    const rect = button.getBoundingClientRect();
    ModalOptions.style.top = `${rect.top + window.scrollY}px`; 
    ModalOptions.style.left = `${rect.left + window.scrollX}px`; 

    ModalOptions.style.display = 'block';
    modalVisible = true;
}

function hideModalActionsContract() {
    const ModalOptions = document.getElementById('ModalOptions');
    ModalOptions.style.display = 'none';
    modalVisible = false;
}

function showModalContract() {
    document.getElementById('AddModalContract').style.display = 'block';
}

function hideModalContract() {
    document.getElementById('AddModalContract').style.display = 'none';
}




function showModalContractName() {
    document.getElementById('EditModalContract').style.display = 'block';
    const contractRow = document.querySelector(`tr[data-id="${selectedContractId}"]`);
    document.getElementById('name').value = contractRow.getAttribute('data-title');
}

function hideModalNameContract() {
    document.getElementById('EditModalContract').style.display = 'none';
}

function saveButtonEditContract() {
    const newTitle = document.getElementById('name').value;
    updateContract(selectedContractId, newTitle);
    hideModalNameContract();
}