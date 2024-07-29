const folderItems = document.getElementsByClassName("folderItem");
const fileItems = document.getElementsByClassName("fileItem");

for (let i = 0; i < folderItems.length; i++) {
    folderItems[i].addEventListener('dblclick', function() {
        const directoryId = this.getAttribute("data-id");
        window.location.href = "/directory/" + directoryId;
    });
}

for(let i=0;i < fileItems.length; i++){
    fileItems[i].addEventListener('dblclick',function() {
        console.log("teste...");
        const fileId = this.getAttribute("data-file-id");
        window.location.href = "/archive/download/"+fileId;
    })
}