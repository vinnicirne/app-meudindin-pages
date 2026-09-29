const fs = require('fs');

const files = ['index.html', 'cadastro.html'];

for (const file of files) {
  if (!fs.existsSync(file)) continue;
  let html = fs.readFileSync(file, 'utf8');

  // Replace Tailwind CDN script with compiled CSS link
  html = html.replace(/<script src="https:\/\/cdn\.tailwindcss\.com"><\/script>/g, '<link href="output.css" rel="stylesheet">');

  // Remove the inline config
  html = html.replace(/<script id="tailwind-config">[\s\S]*?<\/script>/g, '');
  html = html.replace(/<script>\s*tailwind\.config[\s\S]*?<\/script>/g, '');

  // Remove the old style block that was moved to input.css
  html = html.replace(/<style>[\s\S]*?<\/style>/g, '');

  fs.writeFileSync(file, html);
  console.log('Processed ' + file);
}
