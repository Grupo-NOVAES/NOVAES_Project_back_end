let bodyElement = document.querySelector('body');

  BarraLateral = body.querySelector(".BarraLateral"),
  toggle = body.querySelector(".toggle")


toggle.addEventListener("click", () => {
  BarraLateral.classList.toggle("close");
});
