import { Weaver } from "@specs-feup/lara/code/Weaver.ts";
import java from "java";

export default function () {
  Weaver.shutdown();
  java.stop();
}
