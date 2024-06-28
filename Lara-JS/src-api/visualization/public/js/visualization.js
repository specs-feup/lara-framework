const astNodes = document.querySelectorAll('.ast-node');

for (const node of astNodes) {
  node.addEventListener('mouseover', (event) => {
    event.target.classList.toggle('highlighted');
  });
  node.addEventListener('mouseout', (event) => {
    event.target.classList.remove('highlighted');
  });
}