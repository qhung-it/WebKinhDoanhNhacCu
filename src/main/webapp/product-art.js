function svgArt(product) {
  const title = String(product?.brand || product?.name || "Soundora")
    .replace(/[&<>"']/g, "");
  const svg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400"><defs><linearGradient id="g"><stop stop-color="#6C5CE7"/><stop offset="1" stop-color="#00B894"/></linearGradient></defs><rect width="400" height="400" fill="url(#g)"/><circle cx="200" cy="185" r="80" fill="none" stroke="white" stroke-width="16"/><path d="M238 105v145a42 42 0 1 1-20-36V80l110-22v142a42 42 0 1 1-20-36V75z" fill="white"/><text x="200" y="360" text-anchor="middle" font-family="Arial" font-size="22" fill="white">' + title + "</text></svg>";
  return "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svg);
}

function productImg(product, index) {
  const images = Array.isArray(product?.images) ? product.images : [];
  return images[index || 0] || product?.img || svgArt(product || {});
}

function imgTag(product, className, index) {
  const fallback = svgArt(product || {}).replace(/'/g, "%27");
  const src = productImg(product, index).replace(/"/g, "&quot;");
  const alt = String(product?.name || "Sản phẩm").replace(/"/g, "&quot;");
  return '<img class="' + (className || "pimg") + '" src="' + src + '" alt="' + alt + '" loading="lazy" decoding="async" onerror="this.onerror=null;this.src=\'' + fallback + '\'">';
}
