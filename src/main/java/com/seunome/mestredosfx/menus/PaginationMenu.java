package com.seunome.mestredosfx.menus;

import org.bukkit.inventory.Inventory;

public class PaginationMenu {

    private final Inventory inventory;
    private final int itemsPerPage;
    private final int totalItems;
    private int currentPage;
    private final int totalPages;

    public PaginationMenu(Inventory inventory, int itemsPerPage, int totalItems) {
        this.inventory = inventory;
        this.itemsPerPage = itemsPerPage;
        this.totalItems = totalItems;
        this.currentPage = 0;
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNextPage() {
        return currentPage < totalPages - 1;
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (hasPreviousPage()) {
            currentPage--;
        }
    }

    public int getStartIndex() {
        return currentPage * itemsPerPage;
    }

    public int getEndIndex() {
        return Math.min(getStartIndex() + itemsPerPage, totalItems);
    }

    public Inventory getInventory() {
        return inventory;
    }
}

