const btnLogoutElements = document.querySelectorAll(".btnLogout");
const btnProfile = document.getElementById("btnprofile");

function logout() {
    console.log("saindo...");
}

function goProfile() {
	console.log("Indo para Profile");
}


btnLogoutElements.forEach(btn => {
    btn.addEventListener('click', logout);
});

btnProfile.addEventListener('click',goProfile);
