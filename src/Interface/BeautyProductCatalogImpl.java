package Interface;
class BeautyProductCatalogImpl implements BeautyProductCatalog<BeautyProduct> {
    private List<BeautyProduct> products = new ArrayList<>();

    @Override
    public void addProduct(BeautyProduct product) {
        products.add(product);
    }

    @Override
    public boolean removeProduct(String productId) {
        return products.removeIf(p -> p.getId().equals(productId));
    }

    @Override
    public BeautyProduct getProduct(String productId) {
        return products.stream().filter(p -> p.getId().equals(productId)).findFirst().orElse(null);
    }

    @Override
    public List<BeautyProduct> listProducts() {
        return new ArrayList<>(products);
    }
}