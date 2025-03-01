let car = document.getElementById('car');
car.addEventListener("change", () => {
    window.location.replace("http://localhost:8080/insurances/add/" + car.value);
});