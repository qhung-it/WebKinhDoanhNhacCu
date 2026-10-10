/* ============================================================
   Soundora - Main JavaScript
   Yêu cầu: nạp js/data.js trước file này.
   ============================================================ */

/* ---------- Tiện ích ---------- */
const $ = (s, r) => (r || document).querySelector(s);
const $$ = (s, r) => Array.from((r || document).querySelectorAll(s));
const fmt = n => Number(n).toLocaleString('vi-VN') + 'đ';
const esc = s => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const params = new URLSearchParams(location.search);
const PAGE = document.body ? document.body.dataset.page : '';

const store = {
  get(k, d) { try { const v = localStorage.getItem('sdr_' + k); return v ? JSON.parse(v) : d; } catch (e) { return d; } },
  set(k, v) { try { localStorage.setItem('sdr_' + k, JSON.stringify(v)); } catch (e) { /* bỏ qua */ } }
};

const byId = id => PRODUCTS.find(p => p.id === Number(id));
const discountPct = p => (p.old && p.old > p.price) ? Math.round((1 - p.price / p.old) * 100) : 0;

function stars(r) {
  let h = '';
  for (let i = 1; i <= 5; i++) {
    if (r >= i - 0.25) h += '<i class="fas fa-star"></i>';
    else if (r >= i - 0.75) h += '<i class="fas fa-star-half-alt"></i>';
    else h += '<i class="far fa-star"></i>';
  }
  return h;
}

/* ---------- Trạng thái: giỏ hàng, yêu thích ---------- */
let cart = store.get('cart', []);       // [{id, qty}]
let wish = store.get('wish', []);       // [id]

const cartCount = () => cart.reduce((s, i) => s + i.qty, 0);
const cartSubtotal = () => cart.reduce((s, i) => { const p = byId(i.id); return s + (p ? p.price * i.qty : 0); }, 0);

function saveCart() { store.set('cart', cart); updateBadges(); }
function saveWish() { store.set('wish', wish); updateBadges(); }

function updateBadges() {
  $$('.cart-btn .badge').forEach(b => { b.textContent = cartCount(); b.classList.toggle('zero', cartCount() === 0); });
  $$('.wish-btn .badge').forEach(b => { b.textContent = wish.length; b.classList.toggle('zero', wish.length === 0); });
  $$('.btn-wish').forEach(b => {
    const on = wish.includes(Number(b.dataset.id));
    const i = $('i', b);
    if (i) { i.className = (on ? 'fas' : 'far') + ' fa-heart'; i.style.color = on ? '#FF6B6B' : ''; }
  });
}

function addToCart(id, qty, silent) {
  id = Number(id); qty = qty || 1;
  const p = byId(id);
  if (!p) return;
  const item = cart.find(i => i.id === id);
  if (item) item.qty = Math.min(99, item.qty + qty);
  else cart.push({ id, qty });
  saveCart();
  if (!silent) showToast('Đã thêm "' + p.name + '" vào giỏ hàng!');
}

function toggleWish(id) {
  id = Number(id);
  const idx = wish.indexOf(id);
  if (idx >= 0) { wish.splice(idx, 1); showToast('Đã xóa khỏi danh sách yêu thích', 'info'); }
  else { wish.push(id); showToast('Đã thêm vào danh sách yêu thích!'); }
  saveWish();
  if (PAGE === 'wishlist') renderWishlist();
}

/* ---------- Toast ---------- */
function showToast(message, type) {
  const old = $('.toast'); if (old) old.remove();
  const t = document.createElement('div');
  t.className = 'toast';
  const icon = type === 'error' ? 'fa-exclamation-circle' : (type === 'info' ? 'fa-info-circle' : 'fa-check-circle');
  t.innerHTML = '<i class="fas ' + icon + '"' + (type === 'error' ? ' style="color:#FF6B6B"' : '') + '></i> <span></span>';
  $('span', t).textContent = message;
  document.body.appendChild(t);
  setTimeout(() => t.classList.add('show'), 50);
  setTimeout(() => { t.classList.remove('show'); setTimeout(() => t.remove(), 300); }, 3000);
}

/* ---------- Menu mobile (global để dùng được trong HTML nếu cần) ---------- */
function openMobileMenu() {
  const m = $('.mobile-menu'), o = $('.mobile-menu-overlay');
  if (m) m.classList.add('open'); if (o) o.classList.add('open');
  document.body.style.overflow = 'hidden';
}
function closeMobileMenu() {
  const m = $('.mobile-menu'), o = $('.mobile-menu-overlay');
  if (m) m.classList.remove('open'); if (o) o.classList.remove('open');
  document.body.style.overflow = '';
}
window.openMobileMenu = openMobileMenu;
window.closeMobileMenu = closeMobileMenu;

/* ---------- Template card sản phẩm ---------- */
function cardHTML(p, opts) {
  opts = opts || {};
  const d = discountPct(p);
  let badges = '';
  if (p.badge === 'hot') badges = '<span class="badge-tag badge-hot">Bán chạy</span>';
  else if (p.badge === 'new' || p.isNew) badges = '<span class="badge-tag badge-new">Mới</span>';
  else if (d) badges = '<span class="badge-tag badge-sale">-' + d + '%</span>';
  const wishOn = wish.includes(p.id);
  return '' +
    '<div class="product-card' + (opts.promo ? ' promo-product-card' : '') + '">' +
      (opts.promo && d ? '<span class="discount-badge">-' + d + '%</span>' : '') +
      '<div class="card-badges">' + badges + '</div>' +
      '<div class="card-actions">' +
        '<button class="btn-wish" data-id="' + p.id + '" title="Yêu thích" aria-label="Yêu thích"><i class="' + (wishOn ? 'fas' : 'far') + ' fa-heart"' + (wishOn ? ' style="color:#FF6B6B"' : '') + '></i></button>' +
        '<button class="btn-quick" data-id="' + p.id + '" title="Xem nhanh" aria-label="Xem nhanh"><i class="far fa-eye"></i></button>' +
      '</div>' +
      '<a href="product-detail.html?id=' + p.id + '" class="card-image"><span class="product-emoji">' + p.emoji + '</span></a>' +
      '<div class="card-body">' +
        '<div class="card-category">' + esc(p.catName) + ' · ' + esc(p.brand) + '</div>' +
        '<h3 class="card-title"><a href="product-detail.html?id=' + p.id + '">' + esc(p.name) + '</a></h3>' +
        '<div class="card-rating"><span class="stars">' + stars(p.rating) + '</span><span class="rating-text">' + p.rating.toFixed(1) + ' (' + p.reviews + ')</span></div>' +
        '<div class="card-price"><span class="price-current">' + fmt(p.price) + '</span>' + (p.old ? '<span class="price-old">' + fmt(p.old) + '</span>' : '') + '</div>' +
        '<button class="card-btn" data-add="' + p.id + '"><i class="fas fa-cart-plus"></i> Thêm vào giỏ</button>' +
      '</div>' +
    '</div>';
}

function emptyState(icon, title, text, btn) {
  return '<div class="empty-state"><div class="empty-icon">' + icon + '</div><h3>' + title + '</h3><p>' + text + '</p>' + (btn || '') + '</div>';
}

/* ---------- Phân trang dùng chung ---------- */
function renderPager(el, total, page, per, onChange) {
  const pages = Math.ceil(total / per);
  if (!el) return;
  if (pages <= 1) { el.innerHTML = ''; return; }
  const nums = [];
  for (let i = 1; i <= pages; i++) {
    if (i === 1 || i === pages || Math.abs(i - page) <= 1) nums.push(i);
    else if (nums[nums.length - 1] !== '...') nums.push('...');
  }
  let h = '<a href="#" data-p="' + Math.max(1, page - 1) + '" title="Trang trước"><i class="fas fa-chevron-left"></i></a>';
  nums.forEach(n => {
    h += n === '...' ? '<span class="dots">...</span>' : '<a href="#" data-p="' + n + '"' + (n === page ? ' class="active"' : '') + '>' + n + '</a>';
  });
  h += '<a href="#" data-p="' + Math.min(pages, page + 1) + '" title="Trang sau"><i class="fas fa-chevron-right"></i></a>';
  el.innerHTML = h;
  $$('a', el).forEach(a => a.addEventListener('click', e => {
    e.preventDefault();
    onChange(Number(a.dataset.p));
  }));
}

/* ---------- Quick view ---------- */
function openQuickView(id) {
  const p = byId(id); if (!p) return;
  closeQuickView();
  const d = discountPct(p);
  const ov = document.createElement('div');
  ov.className = 'modal-overlay';
  ov.innerHTML =
    '<div class="modal" role="dialog" aria-modal="true">' +
      '<button class="modal-close" aria-label="Đóng"><i class="fas fa-times"></i></button>' +
      '<div class="modal-image">' + p.emoji + '</div>' +
      '<div class="modal-info">' +
        '<div class="card-category">' + esc(p.catName) + ' · ' + esc(p.brand) + '</div>' +
        '<h2>' + esc(p.name) + '</h2>' +
        '<div class="card-rating"><span class="stars">' + stars(p.rating) + '</span><span class="rating-text">' + p.rating.toFixed(1) + ' (' + p.reviews + ' đánh giá)</span></div>' +
        '<div class="card-price"><span class="price-current" style="font-size:1.5rem">' + fmt(p.price) + '</span>' + (p.old ? '<span class="price-old">' + fmt(p.old) + '</span><span class="price-save" style="margin-left:8px;background:#FF6B6B;color:#fff;padding:2px 10px;border-radius:50px;font-size:.75rem">-' + d + '%</span>' : '') + '</div>' +
        '<p class="modal-desc">' + esc(p.short) + '</p>' +
        '<div class="modal-actions">' +
          '<button class="btn btn-primary" data-add="' + p.id + '"><i class="fas fa-cart-plus"></i> Thêm vào giỏ</button>' +
          '<a class="btn btn-outline" href="product-detail.html?id=' + p.id + '">Xem chi tiết</a>' +
        '</div>' +
      '</div>' +
    '</div>';
  document.body.appendChild(ov);
  document.body.style.overflow = 'hidden';
  requestAnimationFrame(() => ov.classList.add('open'));
  ov.addEventListener('click', e => { if (e.target === ov || e.target.closest('.modal-close')) closeQuickView(); });
}
function closeQuickView() {
  const ov = $('.modal-overlay');
  if (ov) { ov.remove(); document.body.style.overflow = ''; }
}

/* ---------- Tìm kiếm trên header ---------- */
function searchProducts(q) {
  q = q.toLowerCase().trim();
  if (!q) return [];
  return PRODUCTS.filter(p => (p.name + ' ' + p.brand + ' ' + p.catName).toLowerCase().includes(q));
}

function initSearch() {
  const bar = $('.search-bar');
  if (!bar) return;
  const input = $('input', bar), btn = $('button', bar);
  if (!input || !btn) return;
  const box = document.createElement('div');
  box.className = 'search-suggest';
  bar.appendChild(box);

  if (PAGE === 'products' && params.get('q')) input.value = params.get('q');

  function go() {
    const q = input.value.trim();
    if (!q) { input.focus(); return; }
    if (PAGE === 'products' && window.__productsSetQuery) { window.__productsSetQuery(q); box.classList.remove('show'); }
    else location.href = 'products.html?q=' + encodeURIComponent(q);
  }
  btn.addEventListener('click', go);
  input.addEventListener('keydown', e => { if (e.key === 'Enter') go(); if (e.key === 'Escape') box.classList.remove('show'); });
  input.addEventListener('input', () => {
    const list = searchProducts(input.value).slice(0, 5);
    if (!input.value.trim()) { box.classList.remove('show'); if (PAGE === 'products' && window.__productsSetQuery) window.__productsSetQuery(''); return; }
    box.innerHTML = list.length
      ? list.map(p => '<a class="suggest-item" href="product-detail.html?id=' + p.id + '"><span class="suggest-emoji">' + p.emoji + '</span><span class="suggest-name">' + esc(p.name) + '</span><span class="suggest-price">' + fmt(p.price) + '</span></a>').join('')
      : '<div class="suggest-empty">Không tìm thấy sản phẩm phù hợp</div>';
    box.classList.add('show');
  });
  document.addEventListener('click', e => { if (!bar.contains(e.target)) box.classList.remove('show'); });
}

/* ---------- Tài khoản ---------- */
const getUser = () => store.get('user', null);
function hash(s) { let h = 5381; for (let i = 0; i < s.length; i++) h = ((h << 5) + h + s.charCodeAt(i)) | 0; return String(h); }

function initUserButton() {
  const u = getUser();
  const a = $('.user-btn');
  if (!a) return;
  a.setAttribute('href', 'login.html');
  a.title = u ? 'Tài khoản: ' + u.name : 'Đăng nhập / Đăng ký';
  if (u) a.innerHTML = '<span class="user-avatar">' + esc(u.name.trim().charAt(0).toUpperCase()) + '</span>';
}

/* ---------- Trang chủ ---------- */
function initHome() {
  const feat = $('[data-render="featured"]');
  if (feat) feat.innerHTML = PRODUCTS.slice().sort((a, b) => b.sold - a.sold).slice(0, 8).map(p => cardHTML(p)).join('');
  const nw = $('[data-render="new"]');
  if (nw) {
    const list = PRODUCTS.filter(p => p.isNew).concat(PRODUCTS.filter(p => !p.isNew).sort((a, b) => b.id - a.id)).slice(0, 8);
    nw.innerHTML = list.map(p => cardHTML(p)).join('');
  }
  const top = $('[data-render="top-selling"]');
  if (top) {
    top.innerHTML = PRODUCTS.slice().sort((a, b) => b.sold - a.sold).slice(0, 5).map((p, i) =>
      '<a class="top-selling-item" href="product-detail.html?id=' + p.id + '">' +
        '<div class="rank' + (i < 3 ? ' top-' + (i + 1) : '') + '">' + (i + 1) + '</div>' +
        '<div class="item-image">' + p.emoji + '</div>' +
        '<div class="item-info"><div class="item-name">' + esc(p.name) + '</div><div class="item-cat">' + esc(p.catName) + '</div></div>' +
        '<div class="item-right"><div class="item-price">' + fmt(p.price) + '</div><div class="item-rating"><i class="fas fa-star"></i> ' + p.rating.toFixed(1) + '</div></div>' +
      '</a>').join('');
  }
  const slider = $('.brand-slider');
  if (slider) slider.innerHTML = BRANDS.slice(0, 12).map(b => '<a class="brand-logo" href="products.html?brand=' + encodeURIComponent(b.name) + '">' + esc(b.name) + '</a>').join('');
}

/* Liên kết danh mục (sidebar + category grid) -> trang sản phẩm có lọc */
function catKeyFromText(t) {
  t = t.replace(/\s+/g, ' ').trim().toLowerCase();
  const c = CATEGORIES.find(c => t.includes(c.name.toLowerCase()));
  return c ? c.key : null;
}
function initCategoryLinks() {
  $$('.sidebar-menu a').forEach(a => {
    const k = catKeyFromText(a.textContent);
    if (k) a.setAttribute('href', 'products.html?cat=' + k);
  });
  $$('.category-item').forEach(el => {
    const k = catKeyFromText(el.textContent);
    if (k) el.addEventListener('click', () => { location.href = 'products.html?cat=' + k; });
  });
  $$('.promo-card').forEach(a => {
    const t = a.textContent.toLowerCase();
    if (t.includes('guitar')) a.setAttribute('href', 'products.html?cat=guitar');
    else if (t.includes('piano')) a.setAttribute('href', 'products.html?cat=piano');
    else if (t.includes('phụ kiện')) a.setAttribute('href', 'products.html?cat=phu-kien');
  });
}

/* ---------- Trang sản phẩm ---------- */
function initProducts() {
  const grid = $('#productsGrid');
  if (!grid) return;
  const state = {
    cats: new Set(params.get('cat') ? params.get('cat').split(',') : []),
    brands: new Set(params.get('brand') ? params.get('brand').split(',') : []),
    min: 0, max: 0, rating: 0, q: params.get('q') || '', sale: params.get('sale') === '1',
    sort: 'featured', page: 1, view: 'grid'
  };

  const side = $('#filterSidebar');
  function buildFilters() {
    const countBy = (fn) => PRODUCTS.reduce((m, p) => { const k = fn(p); m[k] = (m[k] || 0) + 1; return m; }, {});
    const cc = countBy(p => p.cat), bc = countBy(p => p.brand);
    side.innerHTML =
      '<div class="filter-group"><h3>Danh mục sản phẩm <i class="fas fa-chevron-down"></i></h3><div class="filter-content">' +
        CATEGORIES.map(c => '<label class="filter-check"><input type="checkbox" data-f="cat" value="' + c.key + '"' + (state.cats.has(c.key) ? ' checked' : '') + '><span>' + esc(c.name) + '</span><span class="count">(' + (cc[c.key] || 0) + ')</span></label>').join('') +
      '</div></div>' +
      '<div class="filter-group"><h3>Thương hiệu <i class="fas fa-chevron-down"></i></h3><div class="filter-content">' +
        BRANDS.filter(b => bc[b.name]).map(b => '<label class="filter-check"><input type="checkbox" data-f="brand" value="' + esc(b.name) + '"' + (state.brands.has(b.name) ? ' checked' : '') + '><span>' + esc(b.name) + '</span><span class="count">(' + bc[b.name] + ')</span></label>').join('') +
      '</div></div>' +
      '<div class="filter-group"><h3>Khoảng giá <i class="fas fa-chevron-down"></i></h3><div class="filter-content">' +
        '<div class="price-range"><input type="text" inputmode="numeric" id="priceMin" placeholder="Từ (đ)"><span class="separator">-</span><input type="text" inputmode="numeric" id="priceMax" placeholder="Đến (đ)"></div>' +
        '<button class="btn btn-primary btn-sm" id="applyPrice" style="width:100%;margin-top:14px;justify-content:center"><i class="fas fa-filter"></i> Áp dụng lọc</button>' +
      '</div></div>' +
      '<div class="filter-group"><h3>Đánh giá <i class="fas fa-chevron-down"></i></h3><div class="filter-content">' +
        [[4.9, 5, '5 sao'], [4, 4, 'từ 4 sao'], [3, 3, 'từ 3 sao']].map(r =>
          '<label class="filter-check"><input type="radio" name="ratingF" data-f="rating" value="' + r[0] + '"' + (state.rating === r[0] ? ' checked' : '') + '><span>' + '<i class="fas fa-star" style="color:var(--accent-orange)"></i>'.repeat(r[1]) + '<i class="far fa-star" style="color:var(--accent-orange)"></i>'.repeat(5 - r[1]) + ' ' + r[2] + '</span></label>').join('') +
      '</div></div>' +
      '<div class="filter-group"><label class="filter-check"><input type="checkbox" id="saleOnly"' + (state.sale ? ' checked' : '') + '><span>Chỉ sản phẩm đang giảm giá</span></label>' +
      '<button class="btn btn-outline btn-sm" id="clearFilters" style="width:100%;margin-top:6px;justify-content:center"><i class="fas fa-undo"></i> Xóa bộ lọc</button></div>';
  }
  buildFilters();

  function filtered() {
    let list = PRODUCTS.filter(p => {
      if (state.cats.size && !state.cats.has(p.cat)) return false;
      if (state.brands.size && !state.brands.has(p.brand)) return false;
      if (state.min && p.price < state.min) return false;
      if (state.max && p.price > state.max) return false;
      if (state.rating && p.rating < state.rating) return false;
      if (state.sale && !discountPct(p)) return false;
      if (state.q && !(p.name + ' ' + p.brand + ' ' + p.catName).toLowerCase().includes(state.q.toLowerCase())) return false;
      return true;
    });
    const s = state.sort;
    list.sort((a, b) =>
      s === 'price-asc' ? a.price - b.price :
      s === 'price-desc' ? b.price - a.price :
      s === 'name-asc' ? a.name.localeCompare(b.name, 'vi') :
      s === 'name-desc' ? b.name.localeCompare(a.name, 'vi') :
      s === 'newest' ? (Number(b.isNew) - Number(a.isNew)) || (b.id - a.id) :
      s === 'rating' ? b.rating - a.rating || b.reviews - a.reviews :
      b.sold - a.sold);
    return list;
  }

  function render(scroll) {
    const list = filtered();
    const per = SITE.perPage;
    const pages = Math.max(1, Math.ceil(list.length / per));
    if (state.page > pages) state.page = pages;
    const start = (state.page - 1) * per;
    const slice = list.slice(start, start + per);
    grid.className = 'products-grid' + (state.view === 'list' ? ' list-view' : '');
    grid.innerHTML = slice.length ? slice.map(p => cardHTML(p)).join('')
      : emptyState('🔍', 'Không tìm thấy sản phẩm', 'Hãy thử bỏ bớt bộ lọc hoặc đổi từ khóa tìm kiếm.', '<button class="btn btn-primary" id="emptyClear">Xóa bộ lọc</button>');
    $('#resultCount').innerHTML = list.length
      ? 'Hiển thị <strong>' + (start + 1) + ' - ' + (start + slice.length) + '</strong> trong <strong>' + list.length + '</strong> sản phẩm'
      : 'Không có sản phẩm nào';
    renderPager($('#pagination'), list.length, state.page, per, p => { state.page = p; render(true); });
    updateBadges();
    if (scroll) window.scrollTo({ top: 200, behavior: 'smooth' });
  }

  const num = v => Number(String(v).replace(/\D/g, '')) || 0;

  side.addEventListener('change', e => {
    const t = e.target;
    if (t.dataset.f === 'cat') { t.checked ? state.cats.add(t.value) : state.cats.delete(t.value); }
    else if (t.dataset.f === 'brand') { t.checked ? state.brands.add(t.value) : state.brands.delete(t.value); }
    else if (t.dataset.f === 'rating') state.rating = Number(t.value);
    else if (t.id === 'saleOnly') state.sale = t.checked;
    else return;
    state.page = 1; render();
  });
  side.addEventListener('click', e => {
    if (e.target.closest('#applyPrice')) {
      state.min = num($('#priceMin').value); state.max = num($('#priceMax').value);
      if (state.max && state.min > state.max) { showToast('Khoảng giá không hợp lệ', 'error'); return; }
      state.page = 1; render();
    }
    if (e.target.closest('#clearFilters')) clearAll();
    const h = e.target.closest('.filter-group h3 i');
    if (h) {
      const c = $('.filter-content', h.closest('.filter-group'));
      const hidden = c.style.display === 'none';
      c.style.display = hidden ? 'block' : 'none';
      h.style.transform = hidden ? '' : 'rotate(-90deg)';
    }
  });
  ['priceMin', 'priceMax'].forEach(id => {
    const el = $('#' + id);
    el.addEventListener('input', () => { const n = num(el.value); el.value = n ? n.toLocaleString('vi-VN') : ''; });
    el.addEventListener('keydown', e => { if (e.key === 'Enter') $('#applyPrice').click(); });
  });

  function clearAll() {
    state.cats.clear(); state.brands.clear(); state.min = state.max = state.rating = 0; state.sale = false; state.q = ''; state.page = 1;
    const si = $('.search-bar input'); if (si) si.value = '';
    buildFilters(); render();
  }
  document.addEventListener('click', e => { if (e.target.closest('#emptyClear')) clearAll(); });

  $('#sort-select-box').addEventListener('change', e => { state.sort = e.target.value; state.page = 1; render(); });
  $$('.view-toggle button').forEach((b, i) => b.addEventListener('click', () => {
    $$('.view-toggle button').forEach(x => x.classList.remove('active'));
    b.classList.add('active'); state.view = i === 1 ? 'list' : 'grid'; render();
  }));

  window.__productsSetQuery = q => { state.q = q; state.page = 1; render(); };
  render();
}

/* ---------- Trang khuyến mãi ---------- */
function initPromotions() {
  const el = $('[data-render="promo"]');
  if (el) {
    el.innerHTML = PRODUCTS.filter(p => discountPct(p) > 0).sort((a, b) => discountPct(b) - discountPct(a)).slice(0, 8).map(p => cardHTML(p, { promo: true })).join('');
  }
  const cg = $('#couponGrid');
  if (cg) {
    cg.innerHTML = Object.keys(COUPONS).map(code =>
      '<div class="coupon-card"><div class="coupon-left"><i class="fas fa-ticket-alt"></i></div><div class="coupon-body"><strong>' + code + '</strong><span>' + COUPONS[code].desc + '</span></div><button class="btn btn-sm btn-outline" data-copy="' + code + '">Sao chép</button></div>').join('');
  }
}

/* ---------- Đếm ngược khuyến mãi (giữ nguyên mốc giữa các lần tải trang) ---------- */
function initCountdown() {
  const els = $$('.countdown');
  if (!els.length) return;
  let end = store.get('promoEnd', 0);
  if (!end || end < Date.now()) { end = Date.now() + 7 * 24 * 3600 * 1000; store.set('promoEnd', end); }
  function tick() {
    let diff = Math.max(0, end - Date.now());
    const d = Math.floor(diff / 864e5), h = Math.floor(diff % 864e5 / 36e5), m = Math.floor(diff % 36e5 / 6e4), s = Math.floor(diff % 6e4 / 1e3);
    els.forEach(c => {
      const set = (a, v) => { const n = $('[' + a + ']', c); if (n) n.textContent = String(v).padStart(2, '0'); };
      set('data-days', d); set('data-hours', h); set('data-minutes', m); set('data-seconds', s);
    });
  }
  tick(); setInterval(tick, 1000);
}

/* ---------- Chi tiết sản phẩm ---------- */
function seedReviews(p) {
  const names = ['Nguyễn Văn An', 'Trần Thị Mai', 'Lê Hoàng Nam', 'Phạm Quốc Huy', 'Đỗ Thu Hà'];
  const texts = [
    'Sản phẩm đúng như mô tả, đóng gói cẩn thận, giao hàng nhanh. Rất hài lòng!',
    'Chất lượng tốt so với tầm giá. Nhân viên tư vấn nhiệt tình, sẽ ủng hộ tiếp.',
    'Âm thanh rất ổn, dùng một thời gian thấy bền. Mình sẽ giới thiệu cho bạn bè.',
    'Hàng chính hãng, có đầy đủ tem bảo hành. Shop hỗ trợ cài đặt rất nhiệt tình.',
    'Đáng tiền, người mới như mình dùng thấy dễ chơi và thoải mái.'
  ];
  const dates = ['12/09/2026', '03/09/2026', '25/08/2026', '14/08/2026', '30/07/2026'];
  return [0, 1, 2].map(i => {
    const k = (p.id + i) % 5;
    return { name: names[k], rating: i === 2 ? 4 : 5, text: texts[(k + i) % 5], date: dates[(k + i) % 5] };
  });
}

function initDetail() {
  const root = $('#detailRoot');
  if (!root) return;
  const p = byId(params.get('id') || 1);
  if (!p) {
    root.innerHTML = emptyState('😕', 'Không tìm thấy sản phẩm', 'Sản phẩm này không tồn tại hoặc đã ngừng kinh doanh.', '<a class="btn btn-primary" href="products.html">Xem tất cả sản phẩm</a>');
    return;
  }
  document.title = p.name + ' | Soundora';
  const d = discountPct(p);
  const userReviews = store.get('reviews_' + p.id, []);
  const reviews = userReviews.concat(seedReviews(p));
  const total = p.reviews + userReviews.length;

  const r = p.rating;
  const p5 = Math.round(20 + (r - 3.5) * 40), p4 = Math.round((100 - p5) * 0.62), p3 = Math.round((100 - p5 - p4) * 0.6), p2 = Math.round((100 - p5 - p4 - p3) * 0.6), p1 = 100 - p5 - p4 - p3 - p2;
  const dist = [p5, p4, p3, p2, p1];

  const thumbs = [p.emoji, '🎼', '🎵', '🎶'];
  const related = PRODUCTS.filter(x => x.id !== p.id && (x.cat === p.cat || x.brand === p.brand)).slice(0, 4);

  root.innerHTML =
    '<nav class="breadcrumb" aria-label="Breadcrumb"><a href="index.html"><i class="fas fa-home"></i> Trang chủ</a><span class="separator"><i class="fas fa-chevron-right"></i></span><a href="products.html">Sản phẩm</a><span class="separator"><i class="fas fa-chevron-right"></i></span><a href="products.html?cat=' + p.cat + '">' + esc(p.catName) + '</a><span class="separator"><i class="fas fa-chevron-right"></i></span><span class="current">' + esc(p.name) + '</span></nav>' +
    '<section class="product-detail">' +
      '<div class="product-gallery"><div class="product-main-image">' + p.emoji + '</div><div class="product-thumbnails">' +
        thumbs.map((t, i) => '<div class="thumb' + (i === 0 ? ' active' : '') + '">' + t + '</div>').join('') + '</div></div>' +
      '<div class="product-info">' +
        '<h1>' + esc(p.name) + '</h1>' +
        '<div class="card-rating" style="margin-bottom:12px"><span class="stars">' + stars(p.rating) + '</span><span class="rating-text">' + p.rating.toFixed(1) + ' (' + total + ' đánh giá) · Đã bán ' + p.sold + '</span></div>' +
        '<div class="product-meta">' +
          '<span><i class="fas fa-trademark"></i> Thương hiệu: <strong><a href="products.html?brand=' + encodeURIComponent(p.brand) + '" style="color:var(--primary)">' + esc(p.brand) + '</a></strong></span>' +
          '<span><i class="fas fa-barcode"></i> SKU: <strong>' + p.sku + '</strong></span>' +
          '<span><i class="fas fa-check-circle" style="color:var(--accent-green)"></i> Tình trạng: <strong style="color:var(--accent-green)">Còn hàng (' + p.stock + ')</strong></span>' +
        '</div>' +
        '<div class="product-price-box"><span class="price-main">' + fmt(p.price) + '</span>' + (p.old ? '<span class="price-original">' + fmt(p.old) + '</span><span class="price-save">-' + d + '%</span>' : '') + '</div>' +
        '<ul class="detail-features">' + p.features.map(f => '<li><i class="fas fa-check-circle"></i> ' + esc(f) + '</li>').join('') + '</ul>' +
        '<div class="quantity-selector"><label for="product-qty">Số lượng:</label><div class="quantity-controls">' +
          '<button type="button" class="qty-minus" aria-label="Giảm số lượng"><i class="fas fa-minus"></i></button>' +
          '<input type="text" id="product-qty" class="qty-input" value="1" readonly aria-label="Số lượng">' +
          '<button type="button" class="qty-plus" aria-label="Tăng số lượng"><i class="fas fa-plus"></i></button></div></div>' +
        '<div class="product-actions">' +
          '<button type="button" class="btn btn-primary" id="dAdd"><i class="fas fa-cart-plus"></i> Thêm vào giỏ</button>' +
          '<button type="button" class="btn btn-outline" id="dBuy"><i class="fas fa-bolt"></i> Mua ngay</button>' +
          '<button type="button" class="btn btn-outline btn-icon btn-wish" data-id="' + p.id + '" title="Yêu thích" style="flex:0 0 52px;height:52px"><i class="far fa-heart"></i></button>' +
        '</div>' +
        '<div class="product-extra-benefits"><div><i class="fas fa-shield-alt"></i><span>Bảo hành chính hãng 12 tháng</span></div><div><i class="fas fa-shipping-fast"></i><span>Giao hàng nhanh toàn quốc</span></div><div><i class="fas fa-undo-alt"></i><span>Đổi trả 30 ngày miễn phí</span></div></div>' +
      '</div>' +
    '</section>' +
    '<section class="product-tabs">' +
      '<div class="tabs-header">' +
        '<button type="button" class="active" data-tab="desc"><i class="fas fa-file-alt" style="margin-right:8px"></i> Mô tả sản phẩm</button>' +
        '<button type="button" data-tab="specs"><i class="fas fa-list-ul" style="margin-right:8px"></i> Thông số kỹ thuật</button>' +
        '<button type="button" data-tab="reviews"><i class="fas fa-star" style="margin-right:8px"></i> Đánh giá (' + total + ')</button>' +
      '</div>' +
      '<div class="tab-content active" id="desc"><p>' + esc(p.desc) + '</p><p>Mọi sản phẩm tại Soundora đều được kiểm tra kỹ thuật và chỉnh âm trước khi giao đến tay khách hàng.</p></div>' +
      '<div class="tab-content" id="specs"><table class="spec-table">' + p.specs.map(s => '<tr><td>' + esc(s[0]) + '</td><td>' + esc(s[1]) + '</td></tr>').join('') + '</table></div>' +
      '<div class="tab-content" id="reviews">' +
        '<div class="reviews-summary">' +
          '<div class="reviews-summary-score"><div style="font-size:3rem;font-weight:800;color:var(--primary);line-height:1">' + p.rating.toFixed(1) + '</div><div class="stars" style="color:var(--accent-orange);margin:8px 0">' + stars(p.rating) + '</div><div style="font-size:.85rem;color:var(--dark-secondary)">' + total + ' đánh giá</div></div>' +
          '<div>' + dist.map((v, i) => '<div class="review-bar"><span>' + (5 - i) + ' <i class="fas fa-star"></i></span><div class="bar"><div style="width:' + v + '%"></div></div><span>' + v + '%</span></div>').join('') + '</div>' +
          '<div class="reviews-summary-action"><p style="margin-bottom:12px;font-size:.9rem">Bạn đã dùng sản phẩm này?</p><button type="button" class="btn btn-primary btn-sm" id="writeReview">Viết đánh giá</button></div>' +
        '</div>' +
        '<form id="reviewForm" class="review-form" style="display:none" novalidate>' +
          '<h4>Đánh giá của bạn</h4>' +
          '<div class="star-input" id="starInput">' + [1, 2, 3, 4, 5].map(i => '<i class="far fa-star" data-v="' + i + '"></i>').join('') + '</div>' +
          '<div class="form-group"><label for="rvName">Họ tên</label><input type="text" id="rvName" placeholder="Nhập tên của bạn" value="' + esc(getUser() ? getUser().name : '') + '"></div>' +
          '<div class="form-group"><label for="rvText">Nhận xét</label><textarea id="rvText" rows="4" placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm..."></textarea></div>' +
          '<button type="submit" class="btn btn-primary btn-sm">Gửi đánh giá</button>' +
        '</form>' +
        '<div id="reviewList">' + reviews.map(reviewHTML).join('') + '</div>' +
      '</div>' +
    '</section>' +
    (related.length ? '<section class="related-products-section" style="margin-bottom:50px"><div class="section-header"><h2>Sản phẩm liên quan</h2><a href="products.html?cat=' + p.cat + '" class="view-all">Xem tất cả <i class="fas fa-arrow-right"></i></a></div><div class="products-grid">' + related.map(x => cardHTML(x)).join('') + '</div></section>' : '');

  // Hành vi trang chi tiết
  const qty = $('#product-qty');
  $('.qty-minus', root).addEventListener('click', () => { qty.value = Math.max(1, (parseInt(qty.value) || 1) - 1); });
  $('.qty-plus', root).addEventListener('click', () => { qty.value = Math.min(Math.min(99, p.stock), (parseInt(qty.value) || 1) + 1); });
  $('#dAdd').addEventListener('click', () => addToCart(p.id, parseInt(qty.value) || 1));
  $('#dBuy').addEventListener('click', () => { addToCart(p.id, parseInt(qty.value) || 1, true); location.href = 'checkout.html'; });

  const main = $('.product-main-image', root);
  $$('.thumb', root).forEach(t => t.addEventListener('click', () => {
    $$('.thumb', root).forEach(x => x.classList.remove('active')); t.classList.add('active');
    main.style.transform = 'scale(0.95)'; main.style.opacity = '0.7';
    setTimeout(() => { main.textContent = t.textContent; main.style.transform = 'scale(1)'; main.style.opacity = '1'; }, 200);
  }));

  $$('.tabs-header button', root).forEach(b => b.addEventListener('click', () => {
    $$('.tabs-header button', root).forEach(x => x.classList.remove('active'));
    $$('.tab-content', root).forEach(x => x.classList.remove('active'));
    b.classList.add('active'); $('#' + b.dataset.tab, root).classList.add('active');
  }));

  // Đánh giá
  let starVal = 0;
  const form = $('#reviewForm');
  $('#writeReview').addEventListener('click', () => { form.style.display = form.style.display === 'none' ? 'block' : 'none'; });
  const paint = v => $$('#starInput i').forEach(i => { i.className = (Number(i.dataset.v) <= v ? 'fas' : 'far') + ' fa-star'; });
  $$('#starInput i').forEach(i => {
    i.addEventListener('mouseenter', () => paint(Number(i.dataset.v)));
    i.addEventListener('click', () => { starVal = Number(i.dataset.v); paint(starVal); });
  });
  $('#starInput').addEventListener('mouseleave', () => paint(starVal));
  form.addEventListener('submit', e => {
    e.preventDefault();
    const name = $('#rvName').value.trim(), text = $('#rvText').value.trim();
    if (!starVal) return showToast('Vui lòng chọn số sao', 'error');
    if (!name) return showToast('Vui lòng nhập họ tên', 'error');
    if (text.length < 10) return showToast('Nhận xét cần ít nhất 10 ký tự', 'error');
    const list = store.get('reviews_' + p.id, []);
    list.unshift({ name, rating: starVal, text, date: new Date().toLocaleDateString('vi-VN') });
    store.set('reviews_' + p.id, list);
    showToast('Cảm ơn bạn đã đánh giá!');
    initDetail();
    setTimeout(() => { const b = $('.tabs-header button[data-tab="reviews"]'); if (b) b.click(); }, 0);
  });
  updateBadges();
}

function reviewHTML(r) {
  return '<div class="review-item"><div class="review-avatar">' + esc(r.name.trim().charAt(0).toUpperCase()) + '</div><div><div class="review-head"><strong>' + esc(r.name) + '</strong><span class="stars">' + stars(r.rating) + '</span><small>' + esc(r.date) + '</small></div><p>' + esc(r.text) + '</p></div></div>';
}

/* ---------- Giỏ hàng ---------- */
function getCoupon() {
  const code = store.get('coupon', '');
  return code && COUPONS[code] ? code : '';
}
function calcTotals() {
  const sub = cartSubtotal();
  const code = getCoupon();
  const c = code ? COUPONS[code] : null;
  let discount = 0, freeShip = sub >= SITE.freeShipFrom;
  if (c && sub >= c.min) {
    if (c.type === 'percent') discount = Math.min(Math.round(sub * c.value / 100), c.max || Infinity);
    if (c.type === 'amount') discount = c.value;
    if (c.type === 'ship') freeShip = true;
  }
  const ship = (sub === 0 || freeShip) ? 0 : SITE.shipFee;
  return { sub, discount, ship, total: Math.max(0, sub - discount + ship), code: (c && sub >= c.min) ? code : '' };
}

function summaryHTML(t, withButton) {
  return '<div class="summary-box"><h3>Tóm tắt đơn hàng</h3>' +
    '<div class="sum-row"><span>Tạm tính</span><strong>' + fmt(t.sub) + '</strong></div>' +
    '<div class="sum-row"><span>Giảm giá' + (t.code ? ' (' + t.code + ')' : '') + '</span><strong style="color:var(--accent-green)">-' + fmt(t.discount) + '</strong></div>' +
    '<div class="sum-row"><span>Phí vận chuyển</span><strong>' + (t.ship ? fmt(t.ship) : 'Miễn phí') + '</strong></div>' +
    (t.sub > 0 && t.sub < SITE.freeShipFrom ? '<div class="ship-note"><i class="fas fa-truck"></i> Mua thêm ' + fmt(SITE.freeShipFrom - t.sub) + ' để được miễn phí vận chuyển</div>' : '') +
    '<div class="sum-row total"><span>Tổng cộng</span><strong>' + fmt(t.total) + '</strong></div>' +
    (withButton ? '<a href="checkout.html" class="btn btn-primary btn-lg" style="width:100%;justify-content:center;margin-top:16px"><i class="fas fa-lock"></i> Tiến hành thanh toán</a>' : '') +
  '</div>';
}

function renderCart() {
  const root = $('#cartRoot');
  if (!root) return;
  cart = cart.filter(i => byId(i.id));
  if (!cart.length) {
    root.innerHTML = emptyState('🛒', 'Giỏ hàng của bạn đang trống', 'Hãy khám phá các nhạc cụ tuyệt vời của chúng tôi.', '<a href="products.html" class="btn btn-primary">Tiếp tục mua sắm</a>');
    return;
  }
  const t = calcTotals();
  const code = getCoupon();
  root.innerHTML =
    '<div class="cart-layout"><div>' +
      '<div class="cart-list">' + cart.map(i => {
        const p = byId(i.id);
        return '<div class="cart-item" data-id="' + p.id + '">' +
          '<a href="product-detail.html?id=' + p.id + '" class="cart-thumb">' + p.emoji + '</a>' +
          '<div class="cart-info"><a href="product-detail.html?id=' + p.id + '" class="cart-name">' + esc(p.name) + '</a><span class="cart-brand">' + esc(p.brand) + ' · ' + esc(p.catName) + '</span><span class="cart-unit">' + fmt(p.price) + '</span></div>' +
          '<div class="quantity-controls"><button type="button" data-act="minus"><i class="fas fa-minus"></i></button><input type="text" value="' + i.qty + '" readonly><button type="button" data-act="plus"><i class="fas fa-plus"></i></button></div>' +
          '<div class="cart-line">' + fmt(p.price * i.qty) + '</div>' +
          '<button type="button" class="cart-remove" data-act="remove" title="Xóa"><i class="fas fa-trash-alt"></i></button></div>';
      }).join('') + '</div>' +
      '<div style="display:flex;justify-content:space-between;margin-top:16px;flex-wrap:wrap;gap:10px"><a href="products.html" class="btn btn-outline btn-sm"><i class="fas fa-arrow-left"></i> Tiếp tục mua sắm</a><button class="btn btn-outline btn-sm" id="clearCart"><i class="fas fa-trash"></i> Xóa giỏ hàng</button></div>' +
    '</div><div>' +
      '<div class="summary-box" style="margin-bottom:16px"><h3>Mã giảm giá</h3><div class="coupon-row"><input type="text" id="couponInput" placeholder="Nhập mã giảm giá" value="' + esc(code) + '"><button class="btn btn-primary btn-sm" id="applyCoupon">Áp dụng</button></div>' +
        '<small class="text-muted">Thử: SOUNDORA10, FREESHIP, WELCOME50</small></div>' +
      summaryHTML(t, true) +
    '</div></div>';
}

function initCart() {
  const root = $('#cartRoot');
  if (!root) return;
  renderCart();
  root.addEventListener('click', e => {
    const row = e.target.closest('.cart-item');
    const act = e.target.closest('[data-act]');
    if (row && act) {
      const id = Number(row.dataset.id), item = cart.find(i => i.id === id);
      if (act.dataset.act === 'plus') item.qty = Math.min(99, item.qty + 1);
      if (act.dataset.act === 'minus') item.qty = Math.max(1, item.qty - 1);
      if (act.dataset.act === 'remove') { cart = cart.filter(i => i.id !== id); showToast('Đã xóa sản phẩm khỏi giỏ hàng', 'info'); }
      saveCart(); renderCart();
    }
    if (e.target.closest('#clearCart')) { if (confirm('Xóa toàn bộ sản phẩm trong giỏ hàng?')) { cart = []; saveCart(); renderCart(); } }
    if (e.target.closest('#applyCoupon')) {
      const code = $('#couponInput').value.trim().toUpperCase();
      if (!code) { store.set('coupon', ''); renderCart(); return; }
      const c = COUPONS[code];
      if (!c) return showToast('Mã giảm giá không hợp lệ', 'error');
      if (cartSubtotal() < c.min) return showToast('Đơn hàng chưa đạt giá trị tối thiểu ' + fmt(c.min), 'error');
      store.set('coupon', code); showToast('Đã áp dụng mã ' + code); renderCart();
    }
  });
}

/* ---------- Thanh toán ---------- */
function initCheckout() {
  const root = $('#checkoutRoot');
  if (!root) return;
  const u = getUser() || {};
  if (!cart.length) {
    root.innerHTML = emptyState('🛒', 'Giỏ hàng trống', 'Bạn cần thêm sản phẩm trước khi thanh toán.', '<a href="products.html" class="btn btn-primary">Mua sắm ngay</a>');
    return;
  }
  const t = calcTotals();
  root.innerHTML =
    '<form id="checkoutForm" class="checkout-layout" novalidate><div class="checkout-main">' +
      '<div class="summary-box"><h3><i class="fas fa-map-marker-alt"></i> Thông tin nhận hàng</h3>' +
        '<div class="form-row"><div class="form-group"><label for="coName">Họ tên *</label><input id="coName" type="text" value="' + esc(u.name || '') + '" placeholder="Nguyễn Văn A"></div>' +
        '<div class="form-group"><label for="coPhone">Số điện thoại *</label><input id="coPhone" type="tel" value="' + esc(u.phone || '') + '" placeholder="0912345678"></div></div>' +
        '<div class="form-group"><label for="coEmail">Email</label><input id="coEmail" type="email" value="' + esc(u.email || '') + '" placeholder="email@example.com"></div>' +
        '<div class="form-group"><label for="coAddr">Địa chỉ nhận hàng *</label><input id="coAddr" type="text" placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành"></div>' +
        '<div class="form-group"><label for="coNote">Ghi chú</label><textarea id="coNote" rows="3" placeholder="Ghi chú thêm cho đơn hàng (không bắt buộc)"></textarea></div>' +
      '</div>' +
      '<div class="summary-box" style="margin-top:20px"><h3><i class="fas fa-credit-card"></i> Phương thức thanh toán</h3>' +
        [['cod', 'fa-money-bill-wave', 'Thanh toán khi nhận hàng (COD)', 'Kiểm tra hàng trước khi thanh toán'],
         ['bank', 'fa-university', 'Chuyển khoản ngân hàng', 'Soundora Co., Ltd - STK 0123 456 789 - Vietcombank'],
         ['momo', 'fa-wallet', 'Ví MoMo / ZaloPay', 'Thanh toán nhanh qua ví điện tử']
        ].map((m, i) => '<label class="pay-option"><input type="radio" name="pay" value="' + m[0] + '"' + (i === 0 ? ' checked' : '') + '><i class="fas ' + m[1] + '"></i><div><strong>' + m[2] + '</strong><span>' + m[3] + '</span></div></label>').join('') +
      '</div>' +
    '</div><div>' +
      '<div class="summary-box"><h3>Đơn hàng của bạn</h3>' +
        cart.map(i => { const p = byId(i.id); return '<div class="co-item"><span class="co-emoji">' + p.emoji + '</span><div><div>' + esc(p.name) + '</div><small>SL: ' + i.qty + '</small></div><strong>' + fmt(p.price * i.qty) + '</strong></div>'; }).join('') +
      '</div><div style="margin-top:16px">' + summaryHTML(t, false) + '</div>' +
      '<button type="submit" class="btn btn-primary btn-lg" style="width:100%;justify-content:center;margin-top:16px"><i class="fas fa-check-circle"></i> Đặt hàng</button>' +
      '<a href="cart.html" class="btn btn-outline btn-sm" style="width:100%;justify-content:center;margin-top:10px">Quay lại giỏ hàng</a>' +
    '</div></form>';

  $('#checkoutForm').addEventListener('submit', e => {
    e.preventDefault();
    const name = $('#coName').value.trim(), phone = $('#coPhone').value.trim(), email = $('#coEmail').value.trim(), addr = $('#coAddr').value.trim();
    if (name.length < 2) return showToast('Vui lòng nhập họ tên', 'error');
    if (!/^(0|\+84)\d{9,10}$/.test(phone.replace(/\s/g, ''))) return showToast('Số điện thoại không hợp lệ', 'error');
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return showToast('Email không hợp lệ', 'error');
    if (addr.length < 8) return showToast('Vui lòng nhập địa chỉ nhận hàng đầy đủ', 'error');
    const tt = calcTotals();
    const order = {
      code: 'SDR' + Date.now().toString().slice(-8),
      date: new Date().toLocaleString('vi-VN'),
      items: cart.map(i => { const p = byId(i.id); return { id: p.id, name: p.name, emoji: p.emoji, price: p.price, qty: i.qty }; }),
      totals: tt, pay: $('input[name="pay"]:checked').value, status: 'Đang xử lý',
      customer: { name, phone, email, addr, note: $('#coNote').value.trim() },
      user: (getUser() || {}).email || ''
    };
    const orders = store.get('orders', []); orders.unshift(order); store.set('orders', orders);
    cart = []; saveCart(); store.set('coupon', '');
    root.innerHTML = '<div class="empty-state success"><div class="empty-icon">🎉</div><h3>Đặt hàng thành công!</h3><p>Cảm ơn bạn đã mua sắm tại Soundora. Mã đơn hàng của bạn là <strong style="color:var(--primary)">' + order.code + '</strong>.<br>Chúng tôi sẽ liên hệ xác nhận qua số điện thoại ' + esc(phone) + ' trong thời gian sớm nhất.</p><div style="display:flex;gap:12px;justify-content:center;flex-wrap:wrap"><a href="products.html" class="btn btn-primary">Tiếp tục mua sắm</a><a href="login.html" class="btn btn-outline">Xem đơn hàng</a></div></div>';
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
}

/* ---------- Yêu thích ---------- */
function renderWishlist() {
  const root = $('#wishRoot');
  if (!root) return;
  const list = wish.map(byId).filter(Boolean);
  root.innerHTML = list.length
    ? '<div class="products-grid">' + list.map(p => cardHTML(p)).join('') + '</div>'
    : emptyState('💜', 'Chưa có sản phẩm yêu thích', 'Nhấn vào biểu tượng trái tim trên sản phẩm để lưu lại.', '<a href="products.html" class="btn btn-primary">Khám phá sản phẩm</a>');
}

/* ---------- Thương hiệu ---------- */
function initBrands() {
  const root = $('#brandsRoot');
  if (!root) return;
  const input = $('#brandSearch');
  function render() {
    const q = (input.value || '').toLowerCase().trim();
    const list = BRANDS.filter(b => b.name.toLowerCase().includes(q));
    root.innerHTML = list.length ? list.map(b => {
      const n = PRODUCTS.filter(p => p.brand === b.name).length;
      return '<a class="brand-card" href="products.html?brand=' + encodeURIComponent(b.name) + '" style="display:block"><div class="brand-icon">' + esc(b.name.charAt(0)) + '</div><h3>' + esc(b.name) + '</h3><p>' + esc(b.origin) + '</p><p style="margin-top:8px;font-size:.78rem">' + esc(b.desc) + '</p><span class="product-count">' + n + ' sản phẩm</span></a>';
    }).join('') : emptyState('🔍', 'Không tìm thấy thương hiệu', 'Hãy thử từ khóa khác.', '');
  }
  input.addEventListener('input', render);
  render();
}

/* ---------- Tin tức ---------- */
function initNews() {
  const root = $('#newsRoot');
  if (!root) return;
  const cats = ['Tất cả bài viết'].concat(Array.from(new Set(NEWS.map(n => n.cat))));
  let cat = params.get('cat') && cats.includes(params.get('cat')) ? params.get('cat') : cats[0];
  let page = 1;
  const per = 6;
  const pills = $('#newsPills'), pager = $('#newsPager');
  function render() {
    pills.innerHTML = cats.map(c => '<a href="#" data-cat="' + esc(c) + '" class="btn btn-sm ' + (c === cat ? 'btn-primary' : 'btn-outline') + '">' + esc(c) + '</a>').join('');
    const list = NEWS.filter(n => cat === cats[0] || n.cat === cat);
    const slice = list.slice((page - 1) * per, page * per);
    root.innerHTML = slice.map(n =>
      '<article class="news-card"><a class="news-image" href="news-detail.html?id=' + n.id + '" style="background:linear-gradient(135deg,' + n.color + ',' + n.color2 + ')"><span class="news-tag" style="background:' + n.color + '">' + esc(n.cat) + '</span><span style="font-size:4.5rem;filter:drop-shadow(0 4px 12px rgba(0,0,0,.15))">' + n.emoji + '</span></a>' +
      '<div class="news-body"><div class="news-meta"><span><i class="far fa-calendar-alt"></i> ' + n.date + '</span><span><i class="far fa-user"></i> ' + esc(n.author) + '</span></div>' +
      '<h3 class="news-title"><a href="news-detail.html?id=' + n.id + '">' + esc(n.title) + '</a></h3><p class="news-excerpt">' + esc(n.excerpt) + '</p>' +
      '<a href="news-detail.html?id=' + n.id + '" class="read-more">Đọc tiếp <i class="fas fa-arrow-right"></i></a></div></article>').join('');
    renderPager(pager, list.length, page, per, p => { page = p; render(); window.scrollTo({ top: 250, behavior: 'smooth' }); });
  }
  pills.addEventListener('click', e => {
    const a = e.target.closest('[data-cat]');
    if (!a) return; e.preventDefault(); cat = a.dataset.cat; page = 1; render();
  });
  render();
}

function initArticle() {
  const root = $('#articleRoot');
  if (!root) return;
  const n = NEWS.find(x => x.id === Number(params.get('id'))) || null;
  if (!n) { root.innerHTML = emptyState('📰', 'Không tìm thấy bài viết', 'Bài viết không tồn tại hoặc đã bị gỡ.', '<a class="btn btn-primary" href="news.html">Về trang tin tức</a>'); return; }
  document.title = n.title + ' | Soundora';
  const others = NEWS.filter(x => x.id !== n.id && x.cat === n.cat).concat(NEWS.filter(x => x.id !== n.id && x.cat !== n.cat)).slice(0, 3);
  root.innerHTML =
    '<nav class="breadcrumb"><a href="index.html"><i class="fas fa-home"></i> Trang chủ</a><span class="separator"><i class="fas fa-chevron-right"></i></span><a href="news.html">Tin tức</a><span class="separator"><i class="fas fa-chevron-right"></i></span><span class="current">' + esc(n.cat) + '</span></nav>' +
    '<article class="article"><div class="article-hero" style="background:linear-gradient(135deg,' + n.color + ',' + n.color2 + ')">' + n.emoji + '</div>' +
      '<span class="news-tag" style="position:static;display:inline-block;background:' + n.color + ';color:#fff;padding:6px 14px;border-radius:50px;font-size:.75rem;font-weight:600">' + esc(n.cat) + '</span>' +
      '<h1>' + esc(n.title) + '</h1><div class="news-meta"><span><i class="far fa-calendar-alt"></i> ' + n.date + '</span><span><i class="far fa-user"></i> ' + esc(n.author) + '</span></div>' +
      '<p class="article-lead">' + esc(n.excerpt) + '</p>' + n.content.map(c => '<p>' + esc(c) + '</p>').join('') +
      '<div class="article-share"><button class="btn btn-outline btn-sm" id="copyLink"><i class="fas fa-link"></i> Sao chép liên kết</button></div></article>' +
    '<section style="margin-bottom:50px"><div class="section-header"><h2>Bài viết khác</h2></div><div class="news-grid">' +
      others.map(o => '<article class="news-card"><a class="news-image" href="news-detail.html?id=' + o.id + '" style="background:linear-gradient(135deg,' + o.color + ',' + o.color2 + ')"><span class="news-tag" style="background:' + o.color + '">' + esc(o.cat) + '</span><span style="font-size:4rem">' + o.emoji + '</span></a><div class="news-body"><div class="news-meta"><span><i class="far fa-calendar-alt"></i> ' + o.date + '</span></div><h3 class="news-title"><a href="news-detail.html?id=' + o.id + '">' + esc(o.title) + '</a></h3></div></article>').join('') +
    '</div></section>';
  $('#copyLink').addEventListener('click', () => {
    if (navigator.clipboard) navigator.clipboard.writeText(location.href).then(() => showToast('Đã sao chép liên kết!'), () => showToast('Không thể sao chép', 'error'));
    else showToast('Trình duyệt không hỗ trợ sao chép', 'error');
  });
}

/* ---------- Trang thông tin (chính sách) ---------- */
function initInfo() {
  const root = $('#infoRoot');
  if (!root) return;
  const key = INFO_PAGES[params.get('p')] ? params.get('p') : 'about';
  const pg = INFO_PAGES[key];
  document.title = pg.title + ' | Soundora';
  root.innerHTML =
    '<div class="info-layout"><aside class="summary-box info-menu">' +
      Object.keys(INFO_PAGES).map(k => '<a href="info.html?p=' + k + '"' + (k === key ? ' class="active"' : '') + '><i class="fas ' + INFO_PAGES[k].icon + '"></i> ' + INFO_PAGES[k].title + '</a>').join('') +
    '</aside><div class="summary-box info-body"><h2>' + pg.title + '</h2>' + pg.body.map(t => '<p>' + esc(t) + '</p>').join('') + '</div></div>';
}

/* ---------- Đăng nhập / Đăng ký / Tài khoản ---------- */
function initAuth() {
  const root = $('#authRoot');
  if (!root) return;
  function render(tab) {
    const u = getUser();
    if (u) return renderAccount(u);
    tab = tab || 'login';
    root.innerHTML =
      '<div class="auth-wrap"><div class="auth-tabs"><button data-t="login" class="' + (tab === 'login' ? 'active' : '') + '">Đăng nhập</button><button data-t="register" class="' + (tab === 'register' ? 'active' : '') + '">Đăng ký</button></div>' +
      (tab === 'login'
        ? '<form id="loginForm" novalidate><div class="form-group"><label for="lEmail">Email</label><input id="lEmail" type="email" placeholder="email@example.com"></div><div class="form-group"><label for="lPass">Mật khẩu</label><input id="lPass" type="password" placeholder="Nhập mật khẩu"></div><button class="btn btn-primary btn-lg" style="width:100%;justify-content:center">Đăng nhập</button></form>'
        : '<form id="regForm" novalidate><div class="form-group"><label for="rName">Họ tên</label><input id="rName" type="text" placeholder="Nguyễn Văn A"></div><div class="form-row"><div class="form-group"><label for="rEmail">Email</label><input id="rEmail" type="email" placeholder="email@example.com"></div><div class="form-group"><label for="rPhone">Số điện thoại</label><input id="rPhone" type="tel" placeholder="0912345678"></div></div><div class="form-group"><label for="rPass">Mật khẩu (tối thiểu 6 ký tự)</label><input id="rPass" type="password"></div><div class="form-group"><label for="rPass2">Nhập lại mật khẩu</label><input id="rPass2" type="password"></div><button class="btn btn-primary btn-lg" style="width:100%;justify-content:center">Tạo tài khoản</button></form>') +
      '';
    $$('.auth-tabs button', root).forEach(b => b.addEventListener('click', () => render(b.dataset.t)));
    const lf = $('#loginForm');
    if (lf) lf.addEventListener('submit', e => {
      e.preventDefault();
      const email = $('#lEmail').value.trim().toLowerCase(), pass = $('#lPass').value;
      const found = store.get('users', []).find(x => x.email === email && x.pass === hash(pass));
      if (!found) return showToast('Email hoặc mật khẩu không đúng', 'error');
      store.set('user', { name: found.name, email: found.email, phone: found.phone });
      showToast('Đăng nhập thành công!'); initUserButton(); render();
    });
    const rf = $('#regForm');
    if (rf) rf.addEventListener('submit', e => {
      e.preventDefault();
      const name = $('#rName').value.trim(), email = $('#rEmail').value.trim().toLowerCase(), phone = $('#rPhone').value.trim(), p1 = $('#rPass').value, p2 = $('#rPass2').value;
      if (name.length < 2) return showToast('Vui lòng nhập họ tên', 'error');
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return showToast('Email không hợp lệ', 'error');
      if (phone && !/^(0|\+84)\d{9,10}$/.test(phone)) return showToast('Số điện thoại không hợp lệ', 'error');
      if (p1.length < 6) return showToast('Mật khẩu tối thiểu 6 ký tự', 'error');
      if (p1 !== p2) return showToast('Mật khẩu nhập lại không khớp', 'error');
      const users = store.get('users', []);
      if (users.some(x => x.email === email)) return showToast('Email này đã được đăng ký', 'error');
      users.push({ name, email, phone, pass: hash(p1) }); store.set('users', users);
      store.set('user', { name, email, phone });
      showToast('Tạo tài khoản thành công!'); initUserButton(); render();
    });
  }
  function renderAccount(u) {
    const orders = store.get('orders', []).filter(o => !o.user || o.user === u.email);
    root.innerHTML =
      '<div class="account-layout"><div class="summary-box account-card"><div class="user-avatar big">' + esc(u.name.trim().charAt(0).toUpperCase()) + '</div><h3>' + esc(u.name) + '</h3><p>' + esc(u.email) + '</p>' + (u.phone ? '<p>' + esc(u.phone) + '</p>' : '') +
        '<a href="wishlist.html" class="btn btn-outline btn-sm" style="width:100%;justify-content:center;margin-top:12px"><i class="far fa-heart"></i> Yêu thích (' + wish.length + ')</a>' +
        '<button class="btn btn-primary btn-sm" id="logoutBtn" style="width:100%;justify-content:center;margin-top:10px"><i class="fas fa-sign-out-alt"></i> Đăng xuất</button></div>' +
      '<div class="summary-box"><h3>Lịch sử đơn hàng</h3>' + (orders.length ? orders.map(o =>
        '<div class="order-card"><div class="order-head"><strong>#' + o.code + '</strong><span class="order-status">' + esc(o.status) + '</span><small>' + esc(o.date) + '</small></div>' +
        o.items.map(i => '<div class="co-item"><span class="co-emoji">' + i.emoji + '</span><div>' + esc(i.name) + '<br><small>SL: ' + i.qty + '</small></div><strong>' + fmt(i.price * i.qty) + '</strong></div>').join('') +
        '<div class="sum-row total"><span>Tổng cộng</span><strong>' + fmt(o.totals.total) + '</strong></div></div>').join('') : '<p class="text-muted">Bạn chưa có đơn hàng nào.</p>') + '</div></div>';
    $('#logoutBtn').addEventListener('click', () => { store.set('user', null); showToast('Đã đăng xuất', 'info'); initUserButton(); location.reload(); });
  }
  render(params.get('tab') === 'register' ? 'register' : 'login');
}

/* ---------- Liên hệ ---------- */
function initContact() {
  const f = $('.contact-form form');
  if (!f) return;
  f.setAttribute('novalidate', '');
  f.addEventListener('submit', e => {
    e.preventDefault();
    const name = $('#contact-name').value.trim(), email = $('#contact-email').value.trim(), phone = $('#contact-phone').value.trim(), subject = $('#contact-subject').value, msg = $('#contact-message').value.trim();
    if (name.length < 2) return showToast('Vui lòng nhập họ tên', 'error');
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return showToast('Email không hợp lệ', 'error');
    if (!/^(0|\+84)\d{9,10}$/.test(phone.replace(/\s/g, ''))) return showToast('Số điện thoại không hợp lệ', 'error');
    if (!subject) return showToast('Vui lòng chọn chủ đề', 'error');
    if (msg.length < 10) return showToast('Nội dung cần ít nhất 10 ký tự', 'error');
    const list = store.get('contacts', []); list.push({ name, email, phone, subject, msg, date: new Date().toLocaleString('vi-VN') }); store.set('contacts', list);
    showToast('Tin nhắn đã được gửi! Chúng tôi sẽ liên hệ lại sớm nhất.'); f.reset();
  });
}

/* ---------- Khởi tạo chung ---------- */
document.addEventListener('DOMContentLoaded', function () {
  // Menu mobile
  const mt = $('.mobile-toggle'), mo = $('.mobile-menu-overlay'), mc = $('.mobile-menu-close');
  if (mt) mt.addEventListener('click', openMobileMenu);
  if (mo) mo.addEventListener('click', closeMobileMenu);
  if (mc) mc.addEventListener('click', closeMobileMenu);

  // Cuộn trang: header + nút lên đầu
  const header = $('.header'), topBtn = $('.scroll-top');
  window.addEventListener('scroll', () => {
    const y = window.scrollY;
    if (header) header.style.boxShadow = y > 100 ? '0 4px 20px rgba(0,0,0,0.1)' : '0 2px 8px rgba(0,0,0,0.06)';
    if (topBtn) topBtn.classList.toggle('visible', y > 400);
  });
  if (topBtn) topBtn.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));

  // Render theo trang (trước khi gắn observer)
  initUserButton();
  initSearch();
  initCategoryLinks();
  if (PAGE === 'home') initHome();
  if (PAGE === 'products') initProducts();
  if (PAGE === 'promotions') { initPromotions(); initCountdown(); }
  if (PAGE === 'detail') initDetail();
  if (PAGE === 'cart') initCart();
  if (PAGE === 'checkout') initCheckout();
  if (PAGE === 'wishlist') renderWishlist();
  if (PAGE === 'brands') initBrands();
  if (PAGE === 'news') initNews();
  if (PAGE === 'article') initArticle();
  if (PAGE === 'info') initInfo();
  if (PAGE === 'login') initAuth();
  if (PAGE === 'contact') initContact();
  updateBadges();

  // Fade-in khi cuộn
  const io = new IntersectionObserver(entries => entries.forEach(en => {
    if (en.isIntersecting) { en.target.classList.add('visible'); io.unobserve(en.target); }
  }), { threshold: 0.05, rootMargin: '0px 0px -30px 0px' });
  $$('.fade-in').forEach(el => io.observe(el));

  // Sự kiện chung (ủy quyền) - dùng được cho cả nội dung render động
  document.addEventListener('click', e => {
    const add = e.target.closest('[data-add]');
    if (add) {
      e.preventDefault();
      addToCart(add.dataset.add, 1);
      add.style.transform = 'scale(0.95)'; setTimeout(() => { add.style.transform = ''; }, 150);
      return;
    }
    const w = e.target.closest('.btn-wish');
    if (w) { e.preventDefault(); e.stopPropagation(); toggleWish(w.dataset.id); return; }
    const q = e.target.closest('.btn-quick');
    if (q) { e.preventDefault(); openQuickView(q.dataset.id); return; }
    const cp = e.target.closest('[data-copy]');
    if (cp) {
      const code = cp.dataset.copy;
      if (navigator.clipboard) navigator.clipboard.writeText(code).then(() => showToast('Đã sao chép mã ' + code), () => showToast('Mã giảm giá: ' + code, 'info'));
      else showToast('Mã giảm giá: ' + code, 'info');
    }
  });
  document.addEventListener('keydown', e => { if (e.key === 'Escape') { closeQuickView(); closeMobileMenu(); } });

  // Form đăng ký nhận tin
  $$('.newsletter-form, .footer-newsletter-form').forEach(f => f.addEventListener('submit', e => {
    e.preventDefault();
    const i = $('input', f), v = i.value.trim();
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)) return showToast('Vui lòng nhập email hợp lệ', 'error');
    const l = store.get('newsletter', []); if (!l.includes(v)) l.push(v); store.set('newsletter', l);
    showToast('Đăng ký nhận tin thành công!'); i.value = '';
  }));

  // Slider thương hiệu tự cuộn
  const bs = $('.brand-slider');
  if (bs) {
    let dir = 1, paused = false;
    bs.addEventListener('mouseenter', () => { paused = true; });
    bs.addEventListener('mouseleave', () => { paused = false; });
    setInterval(() => {
      if (paused) return;
      bs.scrollLeft += dir;
      if (bs.scrollLeft >= bs.scrollWidth - bs.clientWidth) dir = -1; else if (bs.scrollLeft <= 0) dir = 1;
    }, 30);
  }
});
