let make = document.getElementById('make');
make.addEventListener("change", () => {
    window.location.replace("http://localhost:8080/vehicles/add/" + make.value);
})
