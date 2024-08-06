const folderItems = document.getElementsByClassName("contractItem");

for (let i = 0; i < folderItems.length; i++) {
    folderItems[i].addEventListener('dblclick', function() {
        const contractId = this.getAttribute("data-id");
        window.location.href = "/stages/" + contractId;
    });
}


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