

// Menu Page Logic
let allDishes = [];
let filteredDishes = [];
let currentPage = 1;

document.addEventListener('DOMContentLoaded', async () => {
    await initApp();
    await loadMenu();
    setupFilters();
});

async function loadMenu() {
    try {
        const container = document.getElementById('dishesContainer');
        container.innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>';
        
        allDishes = await api.getMenu();
        filteredDishes = allDishes;
        currentPage = 1;
        displayDishes();
    } catch (error) {
        console.error('Error loading menu:', error);
        document.getElementById('dishesContainer').innerHTML = 
            '<div class="col-12"><div class="alert alert-danger">Error while loading the menu. Please try again later.</div></div>';
    }
}

function setupFilters() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                filterDishes(e.target.value, categoryFilter.value);
            }, 300);
        });
    }
    
    if (categoryFilter) {
        categoryFilter.addEventListener('change', (e) => {
            filterDishes(searchInput?.value || '', e.target.value);
        });
    }
}

function filterDishes(searchQuery, category) {
    filteredDishes = allDishes.filter(dish => {
        const matchesSearch = !searchQuery || 
            dish.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            (dish.description && dish.description.toLowerCase().includes(searchQuery.toLowerCase()));
        
        const matchesCategory = !category || dish.category === category;
        
        return matchesSearch && matchesCategory;
    });
    
    currentPage = 1;
    displayDishes();
}

/** Buduje strony paginacji: jedna strona = jedna pełna kategoria (wszystkie dania z tej kategorii, bez dzielenia). */
function buildPagesByCategory(dishes) {
    const categories = ['PRZYSTAWKA', 'ZUPA', 'DANIE_GLOWNE', 'PIZZA', 'DESER', 'NAPOJ_BEZ_ALKOHOLOWE', 'NAPOJE_ALKOHOLOWE'];
    const byCat = {};
    dishes.forEach(d => {
        if (!byCat[d.category]) byCat[d.category] = [];
        byCat[d.category].push(d);
    });
    const pages = [];
    categories.forEach(cat => {
        const list = byCat[cat] || [];
        if (list.length > 0) pages.push({ category: cat, dishes: list });
    });
    return pages;
}

function displayDishes() {
    const container = document.getElementById('dishesContainer');
    const pages = buildPagesByCategory(filteredDishes);
    const totalPages = pages.length;

    if (totalPages === 0 || filteredDishes.length === 0) {
        container.innerHTML = '<div class="col-12"><div class="alert alert-info">No dishes match your criteria.</div></div>';
        document.getElementById('pagination').innerHTML = '';
        return;
    }

    if (currentPage > totalPages) currentPage = totalPages;
    const pageData = pages[currentPage - 1];
    const { category, dishes: pageDishes } = pageData;

    // Nagłówek kategorii (ta sama na całej stronie)
    let html = `
        <div class="col-12 mb-4 mt-4">
            <h3 class="category-header">
                <i class="bi ${getCategoryIcon(category)} me-2"></i>
                ${getCategoryName(category)}
            </h3>
            <hr class="category-divider">
        </div>
    `;

    pageDishes.forEach((dish, i) => {
        const imageUrl = getDishImageUrl(dish);
        const fallbackUrl = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop';
        const fallbackEscaped = fallbackUrl.replace(/"/g, '&quot;');
        html += `
            <div class="col-md-4 dish-card animate-fade-in" style="animation-delay: ${i * 0.1}s">
                <div class="card h-100">
                    <img src="${imageUrl}" 
                         class="card-img-top" alt="${dish.name}" 
                         style="height: 200px; object-fit: cover;"
                         onerror="this.src='${fallbackEscaped}'">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title dish-name">${dish.name}</h5>
                        <p class="card-text dish-description flex-grow-1">${dish.description || 'No description'}</p>
                        <div class="d-flex justify-content-between align-items-center mt-auto flex-wrap gap-1">
                            <div>
                                ${!dish.available ? '<span class="badge bg-danger">Unavailable</span>' : ''}
                                ${dish.foodType ? `<span class="badge bg-secondary">${getFoodTypeName(dish.foodType)}</span>` : ''}
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <span class="h5 text-primary mb-0">${formatPrice(dish.price)} zł</span>
                            <button class="btn btn-primary" 
                                    onclick="addToCart(${dish.id}, '${dish.name.replace(/'/g, "\\'")}', ${dish.price})"
                                    ${!dish.available ? 'disabled' : ''}>
                                <i class="bi bi-cart-plus"></i> Add
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    });

    container.innerHTML = html;

    createPagination(currentPage, totalPages, (page) => {
        currentPage = page;
        displayDishes();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
}

function getCategoryIcon(category) {
    const icons = {
        'PRZYSTAWKA': 'bi-egg',             // lekka przekąska
        'ZUPA': 'bi-droplet',               // płyn / zupa
        'DANIE_GLOWNE': 'bi-egg-fried',
        'PIZZA': 'bi-disc',
        'DESER': 'bi-cake2-fill',
        'NAPOJ_BEZ_ALKOHOLOWE': 'bi-cup-straw',
        'NAPOJE_ALKOHOLOWE': 'bi-cup'
    };
    return icons[category] || 'bi-circle';
}


function getCategoryName(category) {
    const names = {
        'PRZYSTAWKA': 'Starters',
        'ZUPA': 'Soups',
        'DANIE_GLOWNE': 'Main courses',
        'PIZZA': 'Pizza',
        'DESER': 'Desserts',
        'NAPOJ_BEZ_ALKOHOLOWE': 'Soft drinks',
        'NAPOJE_ALKOHOLOWE': 'Alcoholic drinks'
    };
    return names[category] || category;
}
