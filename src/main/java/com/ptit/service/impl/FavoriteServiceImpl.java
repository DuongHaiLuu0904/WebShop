package com.ptit.service.impl;

import com.ptit.dao.FavoriteDAO;
import com.ptit.entity.Favorite;
import com.ptit.entity.Product;
import com.ptit.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteDAO favoriteDAO;

    @Override
    @Transactional
    public Favorite addToFavorites(Integer customerId, Integer productId) {
        // Kiểm tra xem đã tồn tại chưa
        Favorite existing = favoriteDAO.findByCustomerIdAndProductId(customerId, productId);
        if (existing != null) {
            return existing; // Đã tồn tại, không thêm nữa
        }
        
        // Tạo mới và lưu
        Favorite favorite = new Favorite(customerId, productId);
        return favoriteDAO.save(favorite);
    }

    @Override
    @Transactional
    public void removeFromFavorites(Integer customerId, Integer productId) {
        favoriteDAO.deleteByCustomerIdAndProductId(customerId, productId);
    }

    @Override
    public List<Favorite> getFavoritesByCustomerId(Integer customerId) {
        return favoriteDAO.findByCustomerId(customerId);
    }

    @Override
    public List<Product> getFavoriteProductsByCustomerId(Integer customerId) {
        try {
            List<Favorite> favorites = favoriteDAO.findByCustomerId(customerId);
            
            List<Product> products = favorites.stream()
                    .map(Favorite::getProduct)
                    .filter(product -> product != null) 
                    .collect(Collectors.toList());
                    
            return products;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); 
        }
    }

    @Override
    public boolean isFavorite(Integer customerId, Integer productId) {
        return favoriteDAO.findByCustomerIdAndProductId(customerId, productId) != null;
    }

    @Override
    public Long countFavoritesByCustomerId(Integer customerId) {
        return favoriteDAO.countByCustomerId(customerId);
    }
}
