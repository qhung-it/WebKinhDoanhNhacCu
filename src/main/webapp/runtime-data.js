const SITE = { name: "Soundora", freeShipFrom: 500000, shipFee: 30000, perPage: 12 };
const CATEGORIES = [];
const BRANDS = [];
const CAT_INFO = {};
const PRODUCTS = [];
const COUPONS = {};
const NEWS = [];
const INFO_PAGES = {};
const DEFAULT_TIERS = [];
const TIERS = [];
const PRODUCT_PRESENTATION = [];
window.ALL_PRODUCTS = PRODUCTS;

const SDR = {
  read(key, fallback) {
    try {
      const value = localStorage.getItem("sdr_" + key);
      return value ? JSON.parse(value) : fallback;
    } catch (error) {
      return fallback;
    }
  },
  write(key, value) {
    try {
      localStorage.setItem("sdr_" + key, JSON.stringify(value));
    } catch (error) {
      // Ignore unavailable browser storage.
    }
  },
};

const runtimeContextPath = (() => {
  const firstSegment = location.pathname.split("/").filter(Boolean)[0] || "";
  return firstSegment && !firstSegment.endsWith(".html")
    ? "/" + firstSegment
    : "";
})();

async function runtimeApi(path) {
  const response = await fetch(runtimeContextPath + path, {
    credentials: "same-origin",
  });
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(payload.message || "Không thể tải dữ liệu từ database");
  }
  return payload;
}

function replaceRuntimeArray(target, values) {
  target.splice(0, target.length, ...(Array.isArray(values) ? values : []));
}

async function loadRuntimeContent() {
  const [contentResult, promotionResult] = await Promise.allSettled([
    runtimeApi("/site-content"),
    runtimeApi("/promotions"),
  ]);
  const payload = contentResult.status === "fulfilled" ? contentResult.value : {};
  const promotionRows = promotionResult.status === "fulfilled" && Array.isArray(promotionResult.value)
    ? promotionResult.value
    : [];
  Object.assign(SITE, payload.site || {});
  Object.assign(CAT_INFO, payload.categoryInfo || {});
  Object.assign(INFO_PAGES, payload.infoPages || {});
  Object.keys(COUPONS).forEach((key) => delete COUPONS[key]);
  promotionRows.forEach((promotion) => {
    if (!promotion.code) return;
    COUPONS[String(promotion.code).toUpperCase()] = {
      type: promotion.type,
      value: Number(promotion.value || 0),
      max: Number(promotion.max || 0),
      min: Number(promotion.min || 0),
      start: promotion.start || "",
      end: promotion.end || "",
      active: promotion.active !== false,
      tier: "all",
      desc: promotion.desc || promotion.name || promotion.code,
    };
  });
  replaceRuntimeArray(NEWS, payload.news);
  replaceRuntimeArray(DEFAULT_TIERS, payload.tiers);
  replaceRuntimeArray(TIERS, payload.tiers);
  replaceRuntimeArray(PRODUCT_PRESENTATION, payload.productPresentation);
  return payload;
}

function runtimeLocalId(databaseId, usedIds) {
  const digits = Number(String(databaseId).replace(/\D/g, ""));
  let id = Number.isFinite(digits) && digits > 0
    ? digits
    : Math.abs(Array.from(String(databaseId)).reduce((hash, char) => ((hash * 31 + char.charCodeAt(0)) | 0), 0)) || 1;
  while (usedIds.has(id)) id++;
  usedIds.add(id);
  return id;
}

async function loadCatalogIntoRuntime() {
  const [categoryRows, brandRows, productRows] = await Promise.all([
    runtimeApi("/categories"),
    runtimeApi("/brands"),
    runtimeApi("/products"),
  ]);
  const categoryIcons = new Map(
    (window.runtimeContent?.categories || []).map((row) => [String(row.key || row.id), row.icon]),
  );
  replaceRuntimeArray(CATEGORIES, (categoryRows || []).map((row) => ({
    key: String(row.key || row.id),
    name: row.name || "",
    icon: categoryIcons.get(String(row.key || row.id)) || "🎼",
    active: row.active !== false,
  })));
  const brandContent = new Map(
    (window.runtimeContent?.brands || []).map((row) => [row.name, row]),
  );
  replaceRuntimeArray(BRANDS, (brandRows || []).map((row) => ({
    id: row.id,
    name: row.name || "",
    origin: (brandContent.get(row.name) || {}).origin || "",
    desc: row.desc || "",
  })));
  if (!Array.isArray(productRows)) throw new Error("Dữ liệu sản phẩm từ database không hợp lệ");

  const usedIds = new Set();
  const presentation = new Map(PRODUCT_PRESENTATION.map((row) => [String(row.id), row]));
  const mapped = productRows.map((row) => {
    const databaseId = row.productId || row.id;
    const id = runtimeLocalId(databaseId, usedIds);
    const category = CATEGORIES.find((item) => item.name.toLowerCase() === String(row.categoryName || "").toLowerCase());
    const cat = category ? category.key : String(row.categoryId || "").toLowerCase();
    const extra = presentation.get(String(databaseId)) || {};
    const info = CAT_INFO[cat] || { features: [], specs: [] };
    const brand = row.brandName || "";
    const short = extra.short || row.short || row.description || "";
    return {
      id, dbId: databaseId, name: row.productName || row.name || "", cat,
      catName: row.categoryName || "", brand, emoji: extra.emoji || "🎵",
      price: Number(row.price || 0), old: extra.old ?? null,
      rating: Number(extra.rating || row.rating || 0),
      reviews: Number(extra.reviews || row.reviews || 0), badge: extra.badge || "",
      sold: Number(extra.sold || 0), isNew: Boolean(extra.isNew), short,
      desc: row.description || "", stock: Number(row.quantity ?? row.stock ?? 0),
      sku: extra.sku || databaseId,
      features: [short].concat(info.features || [], ["Sản phẩm chính hãng " + brand]),
      specs: [["Thương hiệu", brand], ["Mã sản phẩm (SKU)", extra.sku || databaseId]].concat(info.specs || []),
      images: row.image ? [row.image] : [],
    };
  });
  replaceRuntimeArray(PRODUCTS, mapped);
  window.ALL_PRODUCTS = PRODUCTS.slice();
}
