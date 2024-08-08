const folderItems = document.getElementsByClassName("contractItem");

for (let i = 0; i < folderItems.length; i++) {
    folderItems[i].addEventListener('dblclick', function() {
        const contractId = this.getAttribute("data-id");
        window.location.href = "/stages/" + contractId;
    });
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
    ModalOptions.style.left = `${rect.right + window.scrollX}px`; 

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

function confirmDeleteContract() {
    if (confirm('Deseja excluir este contrato?')) {
        deleteContract(selectedContractId);
        hideModalActionsContract();
    }
}

function updateContract(id, title) {
    fetch(`/contract/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            title: title
        })
    })
    .then(response => {
        if (response.ok) {
            alert('Contrato atualizado com sucesso!');
            location.reload();
        } else {
            alert('Erro ao atualizar contrato.');
        }
    })
    .catch(error => console.error('Erro:', error));
}


function deleteContract(id) {
    fetch(`/contract/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            alert('Contrato deletado com sucesso!');
            location.reload();
        } else {
            alert('Erro ao deletar contrato.');
        }
    })
    .catch(error => console.error('Erro:', error));
}
