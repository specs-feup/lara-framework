const codeContainer = document.querySelector('#code code');


const highlightNodeAndCode = (node, nodeCode, nodeCodeStart) => {
  node.classList.add('highlighted');

  let codeContainerContents = codeContainer.innerHTML;
  codeContainerContents = codeContainerContents.splice(
    nodeCodeStart,
    nodeCode.length,
    '<span class="highlighted">' + nodeCode + '</span>',
  );
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
  codeContainer.innerHTML = codeContainerContents;
}


const addEventListenersToAstNodes = () => {
  const astNodes = document.querySelectorAll('.ast-node');

  for (const node of astNodes) {
    const nodeCode = escapeHtml(`void matrix_mult(double const *A, double const *B, double *C, int const N, int const M, int const K) {
   for(int ii = 0; ii < N; ii++) {
      for(int jj = 0; jj < K; jj++) {
         //C[i][j] = 0;
         C[K * ii + jj] = 0;
      }
   }
   for(int i = 0; i < N; i++) {
      for(int l = 0; l < M; l++) {
         for(int j = 0; j < K; j++) {
            //C[i][j] += A[i][l]*B[l][j];
            C[K * i + j] += A[M * i + l] * B[K * l + j];
         }
      }
   }
}`);  // TODO: Use real node code

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
