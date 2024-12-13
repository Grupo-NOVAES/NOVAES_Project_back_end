document.addEventListener('DOMContentLoaded', function() {
    const inputs = document.querySelectorAll('input');
    const button = document.querySelector('.CartBtnEdit');
    const requestCode = document.getElementById("requestCode");

    if(requestCode.textContent == "200"){
        alert("Perfil atualziado com sucesso!");
    }else if(requestCode.textContent == "4001"){
        alert("As senhas não coecidem!");
    }else if(requestCode.textContent == "4002"){
        alert("a senha deve ter no minimo 6 caracteres")
    }else if(requestCode.textContent == "500"){
        alert("Ocorre um erro ao salvar as informações...")
    }
    
    const initialValues = {};
    inputs.forEach(input => {
        initialValues[input.placeholder] = input.value;
        input.addEventListener('input', () => {
            let isChanged = false;
            inputs.forEach(input => {
                if (input.value !== initialValues[input.placeholder]) {
                    isChanged = true;
                }
            });
            button.style.display = isChanged ? 'flex' : 'none';
        });
    });
});

document.getElementById('uploadButton').addEventListener('click', function() {
    document.getElementById('fileInput').click();
});

document.getElementById('fileInput').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const formData = new FormData();
        formData.append('imageProfile', file);
        formData.append('userId', document.getElementById('userId').value); // Obtenha o ID do usuário do campo oculto

        fetch('/user/updateProfilePhoto', { // URL do seu endpoint
            method: 'PUT',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                // Recarregue a página após o upload bem-sucedido
                location.reload();
            } else {
                console.error('Erro ao enviar o arquivo');
            }
        })
        .catch(error => {
            console.error('Erro:', error);
        });
    }
});
