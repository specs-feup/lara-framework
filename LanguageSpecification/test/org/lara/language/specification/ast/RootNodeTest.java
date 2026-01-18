/**
 * Copyright 2024 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.language.specification.ast;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RootNode Tests")
class RootNodeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create RootNode with name and alias")
        void shouldCreateRootNodeWithNameAndAlias() {
            RootNode rootNode = new RootNode("TestRoot", "testAlias");
            
            assertThat(rootNode).isNotNull();
            assertThat(rootNode.getRootName()).isEqualTo("TestRoot");
            assertThat(rootNode.getRootAlias()).isEqualTo("testAlias");
            assertThat(rootNode.getChildren()).isEmpty();
        }
        
        @Test
        @DisplayName("Should create RootNode with name only")
        void shouldCreateRootNodeWithNameOnly() {
            RootNode rootNode = new RootNode("TestRoot", null);
            
            assertThat(rootNode).isNotNull();
            assertThat(rootNode.getRootName()).isEqualTo("TestRoot");
            assertThat(rootNode.getRootAlias()).isEmpty(); // Constructor converts null to empty string
        }
        
        @Test
        @DisplayName("Should handle empty name")
        void shouldHandleEmptyName() {
            RootNode rootNode = new RootNode("", "alias");
            
            assertThat(rootNode).isNotNull();
            assertThat(rootNode.getRootName()).isEmpty();
            assertThat(rootNode.getRootAlias()).isEqualTo("alias");
        }
    }
    
    @Nested
    @DisplayName("Child Management Tests")
    class ChildManagementTests {
        
        private RootNode rootNode;
        
        @BeforeEach
        void setUp() {
            rootNode = new RootNode("TestRoot", "testAlias");
        }
        
        @Test
        @DisplayName("Should add JoinPointNode children")
        void shouldAddJoinPointNodeChildren() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            rootNode.addChild(jpNode);
            
            assertThat(rootNode.getChildren()).hasSize(1);
            assertThat(rootNode.getChildren().get(0)).isEqualTo(jpNode);
            assertThat(jpNode.getParent()).isEqualTo(rootNode);
        }
        
        @Test
        @DisplayName("Should add TypeDefNode children")
        void shouldAddTypeDefNodeChildren() {
            TypeDefNode typeDefNode = new TypeDefNode("CustomType");
            
            rootNode.addChild(typeDefNode);
            
            assertThat(rootNode.getChildren()).hasSize(1);
            assertThat(rootNode.getChildren().get(0)).isEqualTo(typeDefNode);
            assertThat(typeDefNode.getParent()).isEqualTo(rootNode);
        }
        
        @Test
        @DisplayName("Should add EnumDefNode children")
        void shouldAddEnumDefNodeChildren() {
            EnumDefNode enumDefNode = new EnumDefNode("CustomEnum");
            
            rootNode.addChild(enumDefNode);
            
            assertThat(rootNode.getChildren()).hasSize(1);
            assertThat(rootNode.getChildren().get(0)).isEqualTo(enumDefNode);
            assertThat(enumDefNode.getParent()).isEqualTo(rootNode);
        }
        
        @Test
        @DisplayName("Should handle multiple children of different types")
        void shouldHandleMultipleChildrenOfDifferentTypes() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            TypeDefNode typeDefNode = new TypeDefNode("CustomType");
            EnumDefNode enumDefNode = new EnumDefNode("CustomEnum");
            
            rootNode.addChild(jpNode);
            rootNode.addChild(typeDefNode);
            rootNode.addChild(enumDefNode);
            
            assertThat(rootNode.getChildren()).hasSize(3);
            assertThat(rootNode.getChildren()).containsExactly(jpNode, typeDefNode, enumDefNode);
        }
    }
    
    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {
        
        @Test
        @DisplayName("Should generate correct toString with alias")
        void shouldGenerateCorrectToStringWithAlias() {
            RootNode rootNode = new RootNode("TestRoot", "testAlias");
            
            String result = rootNode.toString();
            
            assertThat(result).contains("TestRoot");
            assertThat(result).contains("testAlias");
        }
        
        @Test
        @DisplayName("Should generate correct toString without alias")
        void shouldGenerateCorrectToStringWithoutAlias() {
            RootNode rootNode = new RootNode("TestRoot", null);
            
            String result = rootNode.toString();
            
            assertThat(result).contains("TestRoot");
            assertThat(result).doesNotContain("null");
        }
        
        @Test
        @DisplayName("Should generate correct toContentString")
        void shouldGenerateCorrectToContentString() {
            RootNode rootNode = new RootNode("TestRoot", "testAlias");
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            rootNode.addChild(jpNode);
            
            String result = rootNode.toContentString();
            
            assertThat(result).contains("TestRoot");
            assertThat(result).contains("testAlias");
        }
    }
    
    @Nested
    @DisplayName("String Representation and Comparison Tests")
    class StringRepresentationAndComparisonTests {
        
        @Test
        @DisplayName("Should have consistent string representation")
        void shouldHaveConsistentStringRepresentation() {
            RootNode root1 = new RootNode("TestRoot", "testAlias");
            RootNode root2 = new RootNode("TestRoot", "testAlias");
            
            // Same content should have same string representation
            assertThat(root1.toString()).isEqualTo(root2.toString());
            assertThat(root1.toContentString()).isEqualTo(root2.toContentString());
        }
        
        @Test
        @DisplayName("Should not be equal when names differ")
        void shouldNotBeEqualWhenNamesDiffer() {
            RootNode root1 = new RootNode("TestRoot1", "testAlias");
            RootNode root2 = new RootNode("TestRoot2", "testAlias");
            
            // RootNode doesn't override equals, so they're compared by reference
            assertThat(root1).isNotEqualTo(root2);
            // But string representations should be different
            assertThat(root1.toString()).isNotEqualTo(root2.toString());
        }
        
        @Test
        @DisplayName("Should not be equal when aliases differ")
        void shouldNotBeEqualWhenAliasesDiffer() {
            RootNode root1 = new RootNode("TestRoot", "alias1");
            RootNode root2 = new RootNode("TestRoot", "alias2");
            
            // RootNode doesn't override equals, so they're compared by reference
            assertThat(root1).isNotEqualTo(root2);
            // But string representations should be different
            assertThat(root1.toString()).isNotEqualTo(root2.toString());
        }
        
        @Test
        @DisplayName("Should handle null aliases in string representation")
        void shouldHandleNullAliasesInStringRepresentation() {
            RootNode root1 = new RootNode("TestRoot", null);
            RootNode root2 = new RootNode("TestRoot", null);
            RootNode root3 = new RootNode("TestRoot", "alias");
            
            // Same arguments should produce same string representation
            assertThat(root1.toString()).isEqualTo(root2.toString());
            // Different alias should produce different string
            assertThat(root1.toString()).isNotEqualTo(root3.toString());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesAndErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle null name gracefully")
        void shouldHandleNullNameGracefully() {
            // RootNode constructor accepts null name
            RootNode rootNode = new RootNode(null, "alias");
            
            assertThat(rootNode).isNotNull();
            assertThat(rootNode.getRootName()).isNull();
            assertThat(rootNode.getRootAlias()).isEqualTo("alias");
        }
        
        @Test
        @DisplayName("Should maintain parent-child relationships correctly")
        void shouldMaintainParentChildRelationshipsCorrectly() {
            RootNode rootNode = new RootNode("TestRoot", "testAlias");
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            rootNode.addChild(jpNode);
            
            assertThat(jpNode.getParent()).isEqualTo(rootNode);
            assertThat(rootNode.getChildren()).contains(jpNode);
            
            // Test that we can navigate the tree structure
            assertThat(jpNode.getParent()).isInstanceOf(RootNode.class);
            RootNode parentRoot = (RootNode) jpNode.getParent();
            assertThat(parentRoot.getRootName()).isEqualTo("TestRoot");
        }
        
        @Test
        @DisplayName("Should handle large numbers of children")
        void shouldHandleLargeNumbersOfChildren() {
            RootNode rootNode = new RootNode("TestRoot", "testAlias");
            
            // Add many children
            for (int i = 0; i < 1000; i++) {
                JoinPointNode jpNode = new JoinPointNode("JP" + i, "", null);
                rootNode.addChild(jpNode);
            }
            
            assertThat(rootNode.getChildren()).hasSize(1000);
            assertThat(rootNode.getChildren().get(0).getParent()).isEqualTo(rootNode);
            assertThat(rootNode.getChildren().get(999).getParent()).isEqualTo(rootNode);
        }
    }
}
