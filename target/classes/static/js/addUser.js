function toggleFields() {
    const role = document.getElementById("role").value;
    const ifEmployee = document.getElementById("ifEmployee");
    const ifClient = document.getElementById("ifClient");

    if (role === "ADMIN" || role === "EMPLOYEE") {
        ifEmployee.style.display = "block";
        ifClient.style.display = "none";
    } else {
        ifEmployee.style.display = "none";
        ifClient.style.display = "block";
    }
}

toggleFields();