
package Backend.service;

import Backend.model.Item;
import Backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private ItemRepository itemRepository;

    public ItemService() {
        this.itemRepository = new ItemRepository();
    }

    // Constructor for dependency injection (if needed)
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    // Additional methods that leverage the new repository functionality
    public List<Item> getItemsByStatus(String status) {
        return itemRepository.findByStatus(status);
    }

    public List<Item> getItemsByReportedBy(String reportedBy) {
        return itemRepository.findByReportedBy(reportedBy);
    }

    public List<Item> searchItemsByName(String searchTerm) {
        return itemRepository.findByNameContaining(searchTerm);
    }

    public List<Item> searchItemsByDescription(String searchTerm) {
        return itemRepository.findByDescriptionContaining(searchTerm);
    }

    public long getTotalItemCount() {
        return itemRepository.count();
    }

    public long getItemCountByStatus(String status) {
        return itemRepository.countByStatus(status);
    }

    public boolean itemExists(Long id) {
        return itemRepository.existsById(id);
    }
}
