let car = document.getElementById('car');
car.addEventListener("change", () => {
    if (car.value !== null && car.value !== "") {
        window.location.replace("http://localhost:8080/insurances/add/" + car.value);
    } else {
        window.location.replace("http://localhost:8080/insurances/add");
    }
});