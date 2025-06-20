package com.mshop.restapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mshop.entity.Category;
import com.mshop.repository.CategoryRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("api/categories")
public class CategoryRestApi {
	@Autowired
	CategoryRepository repo;

	@RequestMapping()
	public ResponseEntity<List<Category>> getAll() {
		return ResponseEntity.ok(repo.findByStatusTrue());
	}

	@RequestMapping("{id}")
	public ResponseEntity<Category> getOne(@PathVariable("id") Long id) {
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.findById(id).get());
	}

	@PostMapping
	public ResponseEntity<?> post(@RequestBody Category category) {
		// Kiểm tra trùng tên danh mục (chỉ kiểm tra các danh mục đang active)
		if(repo.existsByNameAndStatusTrue(category.getCategoryName())) {
			return ResponseEntity.badRequest().body("Tên danh mục đã tồn tại");
		}

		// Kiểm tra trùng ID (nếu cần)
		if(category.getCategoryId() != null && repo.existsById(category.getCategoryId())) {
			return ResponseEntity.badRequest().body("ID danh mục đã tồn tại");
		}

		return ResponseEntity.ok(repo.save(category));
	}

	@PutMapping("{id}")
	public ResponseEntity<?> put(@RequestBody Category category, @PathVariable("id") Long id) {
		if(!id.equals(category.getCategoryId())) {
			return ResponseEntity.badRequest().body("ID không khớp");
		}
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		// Kiểm tra trùng tên khi cập nhật
		Category existingCategory = repo.findById(id).get();
		if(!existingCategory.getCategoryName().equals(category.getCategoryName())
				&& repo.existsByNameAndStatusTrue(category.getCategoryName())) {
			return ResponseEntity.badRequest().body("Tên danh mục đã tồn tại");
		}

		return ResponseEntity.ok(repo.save(category));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Category ca = repo.findById(id).get();
		ca.setStatus(false);
		repo.save(ca);
		return ResponseEntity.ok().build();
	}
}