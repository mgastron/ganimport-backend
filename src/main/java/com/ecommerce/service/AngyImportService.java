package com.ecommerce.service;

import com.ecommerce.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AngyImportService {
    private static final Logger logger = LoggerFactory.getLogger(AngyImportService.class);
    private static final String BASE_URL = "https://angyimport.smartycart.com.ar/token/b504df6ffa9cddd980e3d518e45e816ab8da4ae2";

    public List<Product> importProducts() {
        List<Product> allProducts = new ArrayList<>();

        try {
            // Obtener primera página y analizar la paginación
            Document doc = Jsoup.connect(BASE_URL).get();
            
            // Buscar el último número de página en la paginación
            Elements paginationLinks = doc.select(".pagination a");
            int totalPages = 1;
            
            if (!paginationLinks.isEmpty()) {
                for (Element link : paginationLinks) {
                    try {
                        String pageNum = link.text().trim();
                        int pageNumber = Integer.parseInt(pageNum);
                        if (pageNumber > totalPages) {
                            totalPages = pageNumber;
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
            
            logger.info("=== INFORMACIÓN DE PAGINACIÓN ===");
            logger.info("Número total de páginas: {}", totalPages);

            // Procesar todas las páginas automáticamente
            for (int page = 1; page <= totalPages; page++) {
                Document docPage = page == 1 ? doc : Jsoup.connect(BASE_URL + "?page=" + page).get();
                Elements pageProducts = docPage.select(".item");
                int productsInPage = pageProducts.size();
                
                for (Element productElement : pageProducts) {
                    Product product = createProductFromElement(productElement);
                    if (product != null) {
                        allProducts.add(product);
                    }
                }
                logger.info("Página {}: {} productos encontrados", page, productsInPage);
            }

            logger.info("=== RESUMEN DE IMPORTACIÓN ===");
            logger.info("Total de productos importados: {}", allProducts.size());

        } catch (Exception e) {
            logger.error("Error al importar productos: {}", e.getMessage());
        }

        return allProducts;
    }

    private Product createProductFromElement(Element productElement) {
        try {
            Product product = new Product();

            // Extraer nombre
            Element nameElement = productElement.selectFirst(".item-name");
            if (nameElement != null) {
                product.setName(nameElement.text().trim());
            }

            // Extraer código
            Element codeElement = productElement.select(".reference").first();
            if (codeElement != null) {
                String code = codeElement.text().trim();
                product.setCode(code);
                logger.debug("Código encontrado: {}", code);
            } else {
                logger.warn("No se encontró el código para el producto");
            }

            // Extraer precio
            Element priceElement = productElement.selectFirst(".item-price");
            if (priceElement != null) {
                String priceText = priceElement.text().replaceAll("[^0-9.]", "");
                try {
                    double price = Double.parseDouble(priceText);
                    product.setPrice(price);
                } catch (NumberFormatException e) {
                    logger.warn("Error al parsear precio: {}", priceText);
                }
            }

            // Extraer imagen
            Element imgElement = productElement.select("img").first();
            if (imgElement != null) {
                String imageUrl = imgElement.attr("data-src"); // Primero intentamos con data-src
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = imgElement.attr("src"); // Si no hay data-src, usamos src
                }
                // Asegurarnos de que la URL sea absoluta
                if (!imageUrl.startsWith("http")) {
                    imageUrl = "https://angyimport.smartycart.com.ar" + (imageUrl.startsWith("/") ? "" : "/") + imageUrl;
                }
                product.setImageUrl(imageUrl);
                logger.debug("URL de imagen encontrada: {}", imageUrl);
            }

            // Extraer cantidad por bulto
            Element quantityElement = productElement.select(".quantity_restrictions").first();
            if (quantityElement != null) {
                String quantityText = quantityElement.text().trim();
                if (quantityText.contains("Bulto:")) {
                    try {
                        String[] parts = quantityText.split("\\|");
                        String bultoText = parts[0].trim();
                        String numberOnly = bultoText.replaceAll("Bulto:", "").trim();
                        int bulkQuantity = Integer.parseInt(numberOnly);
                        product.setBulkQuantity(bulkQuantity);
                    } catch (Exception e) {
                        product.setBulkQuantity(60);
                    }
                } else {
                    product.setBulkQuantity(60);
                }
            }

            // Extraer dimensiones
            Element descElement = productElement.selectFirst(".item-description.description");
            if (descElement != null) {
                String fullDescription = descElement.html();
                String[] lines = fullDescription.split("<br>");
                for (String line : lines) {
                    String cleanLine = line.trim();
                    if (cleanLine.contains("dm") || cleanLine.contains("cm") || cleanLine.contains("mm")) {
                        product.setDimensions(cleanLine);
                        break;
                    }
                }
            }

            // Extraer categorías
            Element categoryElement = productElement.select(".item-categories").first();
            if (categoryElement != null) {
                String categories = categoryElement.text().trim();
                product.setCategories(Arrays.asList(categories.split(",")));
            }

            return product;

        } catch (Exception e) {
            logger.error("Error al crear producto: {}", e.getMessage());
            return null;
        }
    }
} 