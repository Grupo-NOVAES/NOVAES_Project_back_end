const folderItems = document.getElementsByClassName("contractItem");

for (let i = 0; i < folderItems.length; i++) {
    folderItems[i].addEventListener('dblclick', function() {
        const contractId = this.getAttribute("data-id");
        window.location.href = "/stages/" + contractId;
    });
}


<<<<<<< HEAD
=======
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
    ModalOptions.style.left = `${rect.right + window.scrollX}px`; 

    ModalOptions.style.display = 'block';
    modalVisible = true;
}

function hideModalActionsContract() {
    const ModalOptions = document.getElementById('ModalOptions');
    ModalOptions.style.display = 'none';
    modalVisible = false;
}




>>>>>>> 23c8f5e8a6431d698134f39fc3be6f55b1f622b2
function showModalContract() {
    document.getElementById('AddModalContract').style.display = "block";
}

function hideModalContract() {
    document.getElementById('AddModalContract').style.display = "none";
}

function saveButton(){
    console.log('Salvar Add Contrato!');
    hideModalContract();
}



function showModalaActionsContract() {
    document.getElementById('ModalOptions').style.display = "block";
}


function hideModalActionsContract() {
    document.getElementById('ModalOptions').style.display = "none";
}




function showModalContractName() {
    document.getElementById('EditModalContract').style.display = "block";
}

function hideModalNameContract() {
    document.getElementById('EditModalContract').style.display = "none";
}