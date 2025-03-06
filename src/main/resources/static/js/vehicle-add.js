let make = document.getElementById('make');
make.addEventListener("change", () => {
    if (make.value !== null && make.value !== "") {
        window.location.replace("http://localhost:8080/vehicles/add/" + make.value);
    } else {
        window.location.replace("http://localhost:8080/vehicles/add");
    }
});

