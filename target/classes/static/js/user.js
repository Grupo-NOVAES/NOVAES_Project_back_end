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

    const userRow = document.querySelector(`tr[data-id="${selectedUserId}"]`);

    document.getElementById('name').value = userRow.getAttribute('data-name');
    document.getElementById('lastName').value = userRow.getAttribute('data-lastname');
    document.getElementById('email').value = userRow.getAttribute('data-login');
    document.getElementById('number').value = userRow.getAttribute('data-phoneNumber');
    document.getElementById('role').value = userRow.getAttribute('data-role');
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
function updateUser() {
    const userId = selectedUserId;
    const name = document.getElementById('name').value;
    const lastname = document.getElementById('lastName').value;
    const login = document.getElementById('email').value;
    const phoneNumber = document.getElementById('number').value;
    const role = document.getElementById('role').value;

    const formData = new URLSearchParams();
    formData.append('userId', userId);
    formData.append('name', name);
    formData.append('lastname', lastname);
    formData.append('login', login);
    formData.append('phoneNumber', phoneNumber);
    formData.append('role', role);

    fetch('/user/editUser', {
        method: 'PUT',
        body: formData,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log('Usuário atualizado:', data);
        hideModalEditUser();
        location.reload();
    })
    .catch(error => {
        console.error('Erro ao atualizar usuário:', error);
    });

    
}
async function deleteUser(){
    console.log(selectedUserId)
    let response = await fetch(`/user/delete/${selectedUserId}`,{
        method:'DELETE'
    });
    location.reload();
    console.log('User Deletado')
}