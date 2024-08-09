const btnLogoutElements = document.querySelectorAll(".btnLogout");
const btnProfile = document.getElementById("btnprofile");
var goToLogout = false;

function logout() {
    goToLogout=true;
    window.location.href="/logout";
}

function goProfile() {
	if(!goToLogout){
        window.location.href="/user/profile";
        
    }
    goToLogout=false;
}


btnLogoutElements.forEach(btn => {
    btn.addEventListener('click', logout);
});

btnProfile.addEventListener('click',goProfile);
