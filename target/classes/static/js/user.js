document.addEventListener('DOMContentLoaded', function() {
    const userItems = document.querySelectorAll('.userItem');
    userItems.forEach(item => {
        item.addEventListener('dblclick', function() {
            const userId = item.getAttribute('data-id');
            window.location.href=`/user/profile/${userId}`;
        });
    });
});

document.getElementById('searchInput').addEventListener('input', function() {
    const filter = this.value.toLowerCase();
    const rows = document.querySelectorAll('#userTableBody tr');

    rows.forEach(row => {
        const nameCell = row.querySelector('td:nth-child(2)');
        if (nameCell) {
            const nameText = nameCell.textContent.toLowerCase();
            if (nameText.includes(filter)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        }
    });
});


let selectedUserId = null;
let modalVisible = false;

document.addEventListener('click', function(event) {
    const ModalOptions = document.getElementById('ModalOptions');
    const target = event.target;

    if (modalVisible && !ModalOptions.contains(target) && !target.closest('button')) {
        hideModalActionsUser();
    }
});

function showModalActionsUser(button) {
    const ModalOptions = document.getElementById('ModalOptions');
    selectedUserId = button.closest('tr').getAttribute('data-id');

    const rect = button.getBoundingClientRect();
    ModalOptions.style.top = `${rect.top + window.scrollY}px`; 
    ModalOptions.style.left = `${rect.left + window.scrollX}px`; 

    ModalOptions.style.display = 'block';
    modalVisible = true;
}

function hideModalActionsUser() {
    const ModalOptions = document.getElementById('ModalOptions');
    ModalOptions.style.display = 'none';
    modalVisible = false;
}



function showModalEditUser() {
    document.getElementById('EditModalUser').style.display = 'block';
    const contractRow = document.querySelector(`tr[data-id="${selectedUserId}"]`);
    document.getElementById('name').value = contractRow.getAttribute('data-title');
}

function hideModalEditUser() {
    document.getElementById('EditModalUser').style.display = 'none';
}

function saveButtonEditUser() {
    // const newName = document.getElementById('nameUser').value;
    // const newLastName = document.getElementById('lastName').value;
    // const newEmail = document.getElementById('email').value;
    // const newNumber = document.getElementById('number').value;
    // updateUser(selectedUserId, newName, newLastName, newEmail, newNumber)

    updateUser();
    hideModalActionsUser();
}

function confirmDeleteContract() {
    if (confirm('Deseja excluir este contrato?')) {
        deleteUser();
        hideModalActionsUser();
    }
}



function updateUser(id){
    console.log('User Editado')
}

function deleteUser(id){
    console.log('User Deletado')
}