let brand = document.getElementById('brand');
brand.addEventListener("change", () => {
    if (brand.value !== null && brand.value !== "" && brand.value !== "new") {
        window.location.replace("http://localhost:8080/models/add/" + brand.value);
    } else if (brand.value === "new") {
      window.location.replace("http://localhost:8080/models/brands/add");
    } else {
        window.location.replace("http://localhost:8080/vehicles/add");
    }
});