package codex_rishi.ecom_spring.repository;

import codex_rishi.ecom_spring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
   @Query("SELECT p from Product p where "+
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword ,'%'))OR "+
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword ,'%'))OR "+
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword ,'%'))")

    List<Product> searchproducts(String keyword);


    @Query("SELECT p FROM Product p WHERE p.releaseDate >= :fromDate ORDER BY p.releaseDate DESC")
    List<Product> newarrival(@Param("fromDate") Date fromDate);



}
