const folderItems = document.getElementsByClassName("folderItem");
const fileItems = document.getElementsByClassName("fileItem");
let currentItemId = null;

for (let i = 0; i < folderItems.length; i++) {
    folderItems[i].addEventListener('dblclick', function() {
        const directoryId = this.getAttribute("data-id");
        window.location.href = "/directory/" + directoryId;
    });
}

for(let i=0;i < fileItems.length; i++){
    fileItems[i].addEventListener('dblclick',function() {
        const fileId = this.getAttribute("data-file-id");
        window.location.href = "/archive/download/"+fileId;
    })
}



document.addEventListener('DOMContentLoaded', function () {
    const folderItems = document.getElementsByClassName("folderItem");

    for (let i = 0; i < folderItems.length; i++) {

        folderItems[i].addEventListener('dblclick', function() {
            const directoryId = this.getAttribute("data-id");
            window.location.href = "/directory/" + directoryId;
        });

        //  folderItems[i].addEventListener('contextmenu', function(event) {
        //      event.preventDefault();  //Previne o menu de contexto padrão do navegador
            
        //      const contextMenu = document.getElementById('context-menu');
            
        //       //Posiciona o menu de contexto
        //      contextMenu.style.top = `${event.clientY}px`;
        //      contextMenu.style.left = `${event.clientX}px`;
        //      contextMenu.style.display = 'block';
            
            
        //       //Armazena o ID do item clicado
        //      contextMenu.setAttribute('data-id', this.getAttribute("data-id"));
        //      document.getElementById("directoryId").value = contextMenu.getAttribute('data-id')
        //      currentItemId = contextMenu.getAttribute("data-id");
        //  });
        
    }

      //Fecha o menu de contexto quando clicar fora
    //  document.addEventListener('click', function(event) {
    //      const contextMenu = document.getElementById('context-menu');
    //      if (!contextMenu.contains(event.target)) {
    //          contextMenu.style.display = 'none';
    //      }
    //  });

    // Alterna o tema
    const toggleThemeButton = document.getElementById("theme-checkbox");
    const body = document.body;
    const mainElement = document.querySelector('main');
    const headerElement = document.querySelector('header');
    const navElement = document.querySelector('nav');


});





function showModal() {
    document.getElementById('AddFolderModal').style.display = 'block';
}

function hideModalAddFolder() {
    document.getElementById('AddFolderModal').style.display = 'none';
}

function saveButtonAddFolder() {
    console.log('Salvar Pasta');
    let id = document.getElementById("inputParentDirectoryId").value;
    console.log(id);
    hideModalAddFolder();
}



function showModalFile() {
    document.getElementById('AddFolderModalFile').style.display = 'block';
}

function hideModalFile() {
    document.getElementById('AddFolderModalFile').style.display = 'none';
}

function saveButtonFile() {
    console.log('Salvar arquivo');
    hideModalFile();
}



function showModalFolderName() {
    document.getElementById('EditModalFolder').style.display = "block";
}

function hideModalEdit() {
    document.getElementById('EditModalFolder').style.display = "none";
}

function saveButtonEditFolder () {
    console.log('Salvar Edição Pasta!')
    hideModalEdit();
}


function deleteFolder() {
    if (currentItemId === null) {
        alert('Nenhum item selecionado para exclusão.');
        return;
    }

    if (confirm('Você tem certeza que deseja excluir este item?')) {
        fetch(`/directory/delete/${currentItemId}`, {
            method: 'DELETE', // Certifique-se de que o método é 'DELETE'
        })
        .then(response => {
            console.log(JSON.stringify(response))
            if (response.ok) {
                alert('Item excluído com sucesso.');
                location.reload(); // Recarrega a página para atualizar a lista
            } else {
                alert('Erro ao excluir o item. '+ currentItemId);
            }
        })
        .catch(error => {
            console.error('Erro ao excluir o item:', error);
            alert('Erro ao excluir o item.');
        });
    }
    hideModalActionsFile()
}


function dowloadDirectory(){
   window.location.href=`/directory/download/${currentItemId}`;
}

document.getElementById('searchInput').addEventListener('input', function() {
    let filter = this.value.toLowerCase();
    let rows = document.querySelectorAll('#directoryTable tbody tr');
    rows.forEach(row => {
        let text = row.textContent.toLowerCase();
        if (text.includes(filter)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
});









// Refatoração

 let selectedUserId = null;
 let modalVisible = false;

 document.addEventListener('click', function(event) {
     const contextmenu = document.getElementById('ModalOptions');
     const target = event.target;

     if (modalVisible && !contextmenu.contains(target) && !target.closest('button')) {
         hideModalActionsFiles();
     }
 });

 //Aparece o modal dos 3 pontos
 function showModalActionsFile(button) {
     const contextmenu = document.getElementById('ModalOptions');
     selectedUserId = button.closest('tr').getAttribute('data-id');

     //aonde vai aparecer
     const rect = button.getBoundingClientRect();
     contextmenu.style.top = `${rect.top + window.scrollY}px`; 
     contextmenu.style.left = `${rect.left + window.scrollX}px`; 

     contextmenu.style.display = 'block';
     modalVisible = true;     
     currentItemId = selectedUserId
     document.getElementById("directoryId").value = selectedUserId;

 }

 //Esconde o modal de opções de 3 pontos
 function hideModalActionsFile() {
     const contextmenu = document.getElementById('ModalOptions');
     contextmenu.style.display = 'none';
     modalVisible = false;
 }




 let selectedFileId = null;
 let File = false;

 document.addEventListener('click', function(event) {
     const contextmenu = document.getElementById('ModalOptionsFile');
     const target = event.target;

     if (File && !contextmenu.contains(target) && !target.closest('button')) {
         hideModalActionsFiles();
     }
 });

 //Aparece o modal dos 3 pontos
 function showModalActionsFiles(button) {
     const contextmenu = document.getElementById('ModalOptionsFile');
     selectedFileId = button.closest('tr').getAttribute('data-id');

     //aonde vai aparecer
     const rect = button.getBoundingClientRect();
     contextmenu.style.top = `${rect.top + window.scrollY}px`; 
     contextmenu.style.left = `${rect.left + window.scrollX}px`; 

     contextmenu.style.display = 'block';
     File = true;     
     currentItemId = selectedFileId
     document.getElementById("directoryId").value = selectedFileId;

 }

 //Esconde o modal de opções de 3 pontos
 function hideModalActionsFiles() {
     const contextmenu = document.getElementById('ModalOptionsFile');
     contextmenu.style.display = 'none';
     File = false;
 }




 function showModalFileName() {
    document.getElementById('EditModalFolder').style.display = "block";
}

function hideModalEditFile() {
    document.getElementById('EditModalFolder').style.display = "none";
}

function saveButtonEditFile () {
    console.log('Salvar Edição Pasta!')
    hideModalEditFile();
}





function deleteFile() {
    if (currentItemId === null) {
        alert('Nenhum item selecionado para exclusão.');
        return;
    }

    if (confirm('Você tem certeza que deseja excluir este item?')) {
        fetch(`/directory/delete/${currentItemId}`, {
            method: 'DELETE', // Certifique-se de que o método é 'DELETE'
        })
        .then(response => {
            console.log(JSON.stringify(response))
            if (response.ok) {
                alert('Item excluído com sucesso.');
                location.reload(); // Recarrega a página para atualizar a lista
            } else {
                alert('Erro ao excluir o item. '+ currentItemId);
            }
        })
        .catch(error => {
            console.error('Erro ao excluir o item:', error);
            alert('Erro ao excluir o item.');
        });
    }
    hideModalActionsFiles()
}