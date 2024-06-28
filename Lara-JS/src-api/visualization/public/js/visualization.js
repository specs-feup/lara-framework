const codeContainer = document.querySelector('#code code');

const highlightNodeAndCode = (node, nodeCode, nodeCodeStart) => {
  node.classList.add('highlighted');

  let codeContainerContents = codeContainer.innerHTML;
  codeContainerContents = codeContainerContents.splice(
    nodeCodeStart,
    nodeCode.length,
    '<span class="highlighted">' + nodeCode + '</span>',
  );
  console.log(nodeCodeStart, nodeCode.length);
  codeContainer.innerHTML = codeContainerContents;
};


const unhighlightNodeAndCode = (node, nodeCode, nodeCodeStart) => {
  node.classList.remove('highlighted');

  const wrapperLen = '<span class="highlighted">'.length + '</span>'.length;

  let codeContainerContents = codeContainer.innerHTML;
  codeContainerContents = codeContainerContents.splice(
    nodeCodeStart,
    nodeCode.length + wrapperLen,
    nodeCode,
  );
  console.log(nodeCodeStart, nodeCode.length);
  codeContainer.innerHTML = codeContainerContents;
}

const addEventListenersToAstNodes = () => {
  const astNodes = document.querySelectorAll('.ast-node');

  for (const node of astNodes) {
    const nodeCode = "void matrix_mult(double const *A, double const *B, double *C, int const N, int const M, int const K) {";

    const nodeCodeStart = codeContainer.innerHTML.indexOf(nodeCode);
    if (nodeCodeStart === -1) {
      console.warn('Node code not found in code container');
      continue;
    }

    node.addEventListener('mouseover', () => highlightNodeAndCode(node, nodeCode, nodeCodeStart));
    node.addEventListener('mouseout',  () => unhighlightNodeAndCode(node, nodeCode, nodeCodeStart));
  }
};

addEventListenersToAstNodes();
