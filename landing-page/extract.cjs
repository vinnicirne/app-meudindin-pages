const fs = require('fs');
const html = fs.readFileSync('index.html', 'utf8');

const configMatch = html.match(/tailwind\.config=({[\s\S]*?})<\/script>/);
if (configMatch) {
  const configObj = configMatch[1];
  const tailwindConfig = `/** @type {import('tailwindcss').Config} */\nmodule.exports = {\n  content: ["./*.html", "./*.js"],\n  ...(${configObj})\n};`;
  fs.writeFileSync('tailwind.config.cjs', tailwindConfig);
}

const styleMatch = html.match(/<style>([\s\S]*?)<\/style>/);
if (styleMatch) {
  const styles = `@tailwind base;\n@tailwind components;\n@tailwind utilities;\n\n${styleMatch[1]}`;
  fs.writeFileSync('input.css', styles);
}
