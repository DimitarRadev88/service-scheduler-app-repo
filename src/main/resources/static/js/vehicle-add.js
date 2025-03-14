let brand = document.getElementById('brand');
brand.addEventListener("change", () => {
    if (brand.value !== null && brand.value !== "") {
        window.location.replace("http://localhost:8080/vehicles/add/" + brand.value);
    } else {
        window.location.replace("http://localhost:8080/vehicles/add");
    }
});

